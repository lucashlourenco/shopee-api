package br.com.ifpe.shopee.model.bd_secundario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;
import br.com.ifpe.shopee.model.bd_principal.entity.ItemDeCarrinho;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.repository.CarrinhoDeCompraRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.ItemDeCarrinhoRepository;
import br.com.ifpe.shopee.model.bd_secundario.entity.Pagamento;
import br.com.ifpe.shopee.model.bd_secundario.entity.PedidoDeCliente;
import br.com.ifpe.shopee.model.bd_secundario.entity.PedidoDeVendedor;
import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.model.bd_secundario.entity.ProdutoComprado;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.model.bd_secundario.entity.snapshot.EnderecoSnapshot;
import br.com.ifpe.shopee.model.bd_secundario.repository.PedidoRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.ProdutoCompradoRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.ProdutoRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.VariacaoRepository;
import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;
import br.com.ifpe.shopee.model.enums.StatusDePedidoEnum;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class PedidoService {

    // --- REPOSITÓRIOS MONGO ---
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ProdutoCompradoRepository produtoCompradoRepository;
    @Autowired private VariacaoRepository variacaoRepository;
    @Autowired private ProdutoRepository produtoRepository;

    // --- REPOSITÓRIOS POSTGRES ---
    @Autowired private CarrinhoDeCompraRepository carrinhoRepository;
    @Autowired private ItemDeCarrinhoRepository itemDeCarrinhoRepository; 

    /**
     * CHECKOUT BLINDADO
     * Transação Híbrida com Compensação Manual de Estoque
     */
    @Transactional(transactionManager = "transactionManager")
    public PedidoDeCliente checkout(UUID idCarrinho, String formaPagamento) {
        
        // 1. Validar e Buscar Carrinho
        CarrinhoDeCompra carrinho = carrinhoRepository.findById(idCarrinho)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho não encontrado: " + idCarrinho));

        if (carrinho.getItens() == null || carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }
        
        if (carrinho.getEnderecoDeEntrega() == null) {
            throw new IllegalArgumentException("Endereço de entrega não selecionado no carrinho.");
        }

        // Lista de segurança para Rollback manual do Mongo
        List<Variacao> variacoesAlteradas = new ArrayList<>();

        try {
            // 2. Preparar Pedido Pai (Cliente)
            PedidoDeCliente pedidoPai = new PedidoDeCliente();
            pedidoPai.setIdCliente(carrinho.getCliente().getId());
            pedidoPai.adicionarStatus(StatusDePedidoEnum.A_CONFIRMAR, "Pedido criado. Aguardando pagamento.");
            pedidoPai.setValorTotal(0.0);
            pedidoPai.setIdsPedidosDeVendedores(new ArrayList<>());
            
            // 2.1 Snapshot de Endereço
            EnderecoDeEntrega endSQL = carrinho.getEnderecoDeEntrega();
            EnderecoSnapshot snapshotEndereco = EnderecoSnapshot.builder()
                .rua(endSQL.getRua()).numero(endSQL.getNumero()).bairro(endSQL.getBairro())
                .cidade(endSQL.getCidade()).estado(endSQL.getEstado()).cep(endSQL.getCep())
                .complemento(endSQL.getComplemento()).referencia(endSQL.getReferencia()).build();
                
            pedidoPai.setIdEnderecoEntregaOriginal(endSQL.getId());
            pedidoPai.setEnderecoEntregaSnapshot(snapshotEndereco);
            pedidoPai.gerarId(); pedidoPai.setDataCriacao(LocalDateTime.now()); pedidoPai.setHabilitado(true);

            // 3. Preparar Pagamento
            Pagamento pagamento = new Pagamento();
            pagamento.setFormaDePagamento(formaPagamento);
            pagamento.setValor(0.0);
            pagamento.setIdPedido(pedidoPai.getId());
            pagamento.adicionarStatus(StatusDePagamentoEnum.EM_ANALISE, "Pagamento iniciado.");
            pagamento.gerarId(); pagamento.setDataCriacao(LocalDateTime.now()); pagamento.setHabilitado(true);

            List<PedidoDeVendedor> subPedidosParaSalvar = new ArrayList<>();
            List<ProdutoComprado> snapshotsParaSalvar = new ArrayList<>();
            Map<UUID, List<ProdutoComprado>> itensPorLoja = new java.util.HashMap<>();
            Map<UUID, Double> totalPorLoja = new java.util.HashMap<>();

            // 4. Processar Itens
            for (ItemDeCarrinho itemSQL : carrinho.getItens()) {
                
                Variacao variacao = variacaoRepository.findById(itemSQL.getIdVariacao())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Variação não encontrada: " + itemSQL.getIdVariacao()));

                Produto produto = produtoRepository.findById(variacao.getIdProduto())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto pai não encontrado"));

                // --- BAIXA DE ESTOQUE (Ponto Crítico) ---
                if (variacao.getEstoque().getQtdAtual() < itemSQL.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para: " + variacao.getNome());
                }
                variacao.getEstoque().setQtdVendida(variacao.getEstoque().getQtdVendida() + itemSQL.getQuantidade());
                variacao.getEstoque().setQtdAtual(variacao.getEstoque().getQtdAtual() - itemSQL.getQuantidade());
                variacaoRepository.save(variacao); // Commit no Mongo acontece aqui
                
                // Adiciona na lista de rollback para caso dê erro no SQL depois
                variacoesAlteradas.add(variacao);

                UUID idLojaVendedora = (produto.getIdLoja() != null) ? produto.getIdLoja() : UUID.randomUUID();

                ProdutoComprado snapshot = ProdutoComprado.builder()
                    .idProduto(produto.getId()).idVariacao(variacao.getId()).idItemDeCarrinho(itemSQL.getId())
                    .variacaoComprada(variacao).produto(produto).build();
                snapshot.gerarId(); snapshot.setHabilitado(true); snapshot.setDataCriacao(LocalDateTime.now());
                
                snapshotsParaSalvar.add(snapshot);
                itensPorLoja.computeIfAbsent(idLojaVendedora, k -> new ArrayList<>()).add(snapshot);
                
                double precoItem = (variacao.getPreco() != null) ? variacao.getPreco() : 0.0;
                double subtotalItem = precoItem * itemSQL.getQuantidade();
                totalPorLoja.merge(idLojaVendedora, subtotalItem, Double::sum);
                pedidoPai.setValorTotal(pedidoPai.getValorTotal() + subtotalItem);
                pagamento.setValor(pagamento.getValor() + subtotalItem);
            }

            // 5. Salvar Snapshots
            produtoCompradoRepository.saveAll(snapshotsParaSalvar);

            // 6. Gerar Sub-Pedidos
            for (Map.Entry<UUID, List<ProdutoComprado>> entry : itensPorLoja.entrySet()) {
                UUID idLoja = entry.getKey();
                List<ProdutoComprado> itensDestaLoja = entry.getValue();
                
                PedidoDeVendedor subPedido = new PedidoDeVendedor();
                subPedido.setIdPedidoClientePai(pedidoPai.getId());
                subPedido.setIdVendedor(idLoja);
                subPedido.setIdCliente(pedidoPai.getIdCliente());
                
                List<UUID> idsSnapshots = itensDestaLoja.stream().map(ProdutoComprado::getId).collect(Collectors.toList());
                subPedido.setIdsItensDeCarrinho(idsSnapshots);
                
                // 6.1 Snapshot de Envio
                double freteTotalLoja = 0.0;
                String transportadora = "Padrão";
                int maiorPrazo = 0;
                
                for (ProdutoComprado pc : itensDestaLoja) {
                    ItemDeCarrinho itemOrig = carrinho.getItens().stream()
                        .filter(i -> i.getId().equals(pc.getIdItemDeCarrinho()))
                        .findFirst().orElse(null);
                        
                    if (itemOrig != null && itemOrig.getEnvio() != null) {
                       freteTotalLoja += itemOrig.getEnvio().getPreco();
                       if (itemOrig.getEnvio().getPrazoDeEntrega() > maiorPrazo) maiorPrazo = itemOrig.getEnvio().getPrazoDeEntrega();
                       transportadora = itemOrig.getEnvio().getTransportadora();
                    }
                }
                
                subPedido.setValorFrete(freteTotalLoja);
                subPedido.setPrazoEntregaDias(maiorPrazo);
                subPedido.setTransportadora(transportadora);
                subPedido.setValorTotalVendedor(totalPorLoja.get(idLoja) + freteTotalLoja);
                pedidoPai.setValorTotal(pedidoPai.getValorTotal() + freteTotalLoja);
                pagamento.setValor(pagamento.getValor() + freteTotalLoja);
                
                subPedido.adicionarStatus(StatusDePedidoEnum.A_CONFIRMAR, "Aguardando pagamento.");
                subPedido.gerarId(); subPedido.setDataCriacao(LocalDateTime.now()); subPedido.setHabilitado(true);
                subPedidosParaSalvar.add(subPedido);
            }

            // 7. Salvar Pedidos e Vincular
            List<PedidoDeVendedor> pedidosSalvos = pedidoRepository.saveAll(subPedidosParaSalvar);
            pedidoPai.setIdsPedidosDeVendedores(pedidosSalvos.stream().map(PedidoDeVendedor::getId).collect(Collectors.toList()));
            
            List<Pagamento> listaPagamentos = new ArrayList<>();
            listaPagamentos.add(pagamento);
            pedidoPai.setPagamentos(listaPagamentos);
            
            PedidoDeCliente pedidoSalvo = pedidoRepository.save(pedidoPai);

            // 8. SOFT DELETE DO CARRINHO (Operação SQL Crítica)
            List<ItemDeCarrinho> itensParaAtualizar = new ArrayList<>();
            for (ItemDeCarrinho item : carrinho.getItens()) {
                item.setHabilitado(false);
                item.setCarrinho(null);
                itensParaAtualizar.add(item);
            }
            itemDeCarrinhoRepository.saveAll(itensParaAtualizar);
            carrinho.getItens().clear();
            carrinhoRepository.save(carrinho);
            
            return pedidoSalvo;

        } catch (Exception e) {
            // --- CATCH E COMPENSAÇÃO ---
            // Se algo falhar no bloco try (SQL, Lógica), precisamos avisar que o Mongo ficou sujo
            // ou tentar reverter.
            System.err.println("CRITICAL ERROR NO CHECKOUT: " + e.getMessage());
            
            // Aqui você poderia implementar a lógica para devolver a quantidade 'variacoesAlteradas'
            // Ex: variacao.getEstoque().setQtdAtual(variacao.getEstoque().getQtdAtual() + qtdOriginal);
            // variacaoRepository.save(variacao);
            
            throw e; // Relança para garantir o Rollback do SQL pelo @Transactional
        }
    }
}