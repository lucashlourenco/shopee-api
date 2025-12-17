package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;
import br.com.ifpe.shopee.model.bd_principal.entity.ItemDeCarrinho;
import br.com.ifpe.shopee.model.bd_principal.repository.CarrinhoDeCompraRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.ItemDeCarrinhoRepository;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.model.bd_secundario.repository.VariacaoRepository;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoDeCompraRepository carrinhoRepository;

    @Autowired
    private ItemDeCarrinhoRepository itemRepository;

    @Autowired
    private VariacaoRepository variacaoRepository;

    @Transactional
    public CarrinhoDeCompra adicionarItem(UUID idCliente, UUID idVariacao, int quantidade) {
        
        // 1. Busca Segura usando SEU método do Repository
        Variacao variacao = variacaoRepository.findByIdAndHabilitadoTrue(idVariacao)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Variação não encontrada ou indisponível: " + idVariacao));

        // 2. Validação de Estoque (Agora funciona pois adicionamos qtdAtual no Estoque)
        if (variacao.getEstoque() == null || variacao.getEstoque().getQtdAtual() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        // 3. Busca ou Cria Carrinho
        CarrinhoDeCompra carrinho = buscarOuCriarCarrinho(idCliente);

        // 4. Lógica de Item (Soma ou Cria)
        Optional<ItemDeCarrinho> itemExistente = carrinho.getItens().stream()
            .filter(item -> item.getIdVariacao().equals(idVariacao))
            .findFirst();

        if (itemExistente.isPresent()) {
            ItemDeCarrinho item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + quantidade;
            
            if (variacao.getEstoque().getQtdAtual() < novaQuantidade) {
                throw new IllegalArgumentException("Estoque insuficiente para adicionar mais itens.");
            }
            item.setQuantidade(novaQuantidade);
        } else {
            ItemDeCarrinho novoItem = ItemDeCarrinho.builder()
                .carrinho(carrinho)
                .idVariacao(idVariacao)
                .quantidade(quantidade)
                .build();
            novoItem.gerarId();
            novoItem.setHabilitado(true);
            carrinho.getItens().add(novoItem);
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void removerItem(UUID idItem) {
        itemRepository.deleteById(idItem);
    }

    public CarrinhoDeCompra buscarCarrinhoDoCliente(UUID idCliente) {
        CarrinhoDeCompra carrinho = buscarOuCriarCarrinho(idCliente);
        
        // Hidratação dos dados transientes (Preenche os dados do produto que vêm do Mongo)
        for (ItemDeCarrinho item : carrinho.getItens()) {
            variacaoRepository.findById(item.getIdVariacao())
                .ifPresent(item::setVariacao); 
        }
        return carrinho;
    }

    private CarrinhoDeCompra buscarOuCriarCarrinho(UUID idCliente) {
        return carrinhoRepository.findByClienteIdAndHabilitadoTrue(idCliente)
            .orElseGet(() -> {
                CarrinhoDeCompra novo = new CarrinhoDeCompra();
                // TODO: Setar Cliente aqui (novo.setCliente(...))
                
                novo.setItens(new ArrayList<>());
                novo.gerarId();
                novo.setHabilitado(true);
                
                // Agora funciona pois CarrinhoDeCompra herda de EntidadeAuditavelJPA
                novo.setDataCriacao(LocalDateTime.now()); 
                
                return carrinhoRepository.save(novo);
            });
    }
}