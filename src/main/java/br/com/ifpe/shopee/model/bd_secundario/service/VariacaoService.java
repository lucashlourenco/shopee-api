package br.com.ifpe.shopee.model.bd_secundario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.model.bd_secundario.entity.Caracteristica;
import br.com.ifpe.shopee.model.bd_secundario.entity.Estoque;
import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.model.bd_secundario.repository.CaracteristicaRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.ProdutoRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.VariacaoRepository;
import br.com.ifpe.shopee.model.bd_secundario.request.VariacaoRequest;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class VariacaoService {

    @Autowired
    private VariacaoRepository variacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    // =========================================================================
    // CREATE
    // =========================================================================
    public Variacao adicionarVariacao(VariacaoRequest request) {
        if (request.getIdProduto() == null) throw new IllegalArgumentException("ID do produto é obrigatório.");
        
        Produto produto = produtoRepository.findByIdAndHabilitadoTrue(request.getIdProduto())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + request.getIdProduto()));

        // 1. Constrói Variação (Sem SKU)
        Variacao variacao = construirVariacao(request);
        
        // 2. Salva Características
        List<Caracteristica> carsSalvas = salvarCaracteristicas(request.getCaracteristicas());
        variacao.setCaracteristicas(carsSalvas);

        // 3. Auditoria e Salvar
        variacao.gerarId();
        if (variacao.getId() == null) variacao.setId(UUID.randomUUID());
        
        variacao.setIdProduto(produto.getId());
        variacao.setHabilitado(true);
        variacao.setDataCriacao(LocalDateTime.now());

        return variacaoRepository.save(variacao);
    }

    // =========================================================================
    // UPDATE
    // =========================================================================
    public Variacao atualizarVariacao(UUID id, VariacaoRequest request) {
        Variacao variacao = buscarPorId(id);

        // Atualiza campos simples
        variacao.setNome(request.getNome());
        variacao.setDescricao(request.getDescricao());
        variacao.setEstado(request.getEstado());
        variacao.setStatus(request.getStatus());
        variacao.setFotos(request.getFotos());
        variacao.setPeso(request.getPeso());
        variacao.setTamanhoDeEnvioX(request.getTamanhoDeEnvioX());
        variacao.setTamanhoDeEnvioY(request.getTamanhoDeEnvioY());
        variacao.setTamanhoDeEnvioZ(request.getTamanhoDeEnvioZ());

        // Atualiza Estoque
        if (variacao.getEstoque() == null) variacao.setEstoque(new Estoque());
        variacao.getEstoque().setPreco(request.getEstoque().getPreco());
        variacao.getEstoque().setQtdInicial(request.getEstoque().getQtdInicial());

        variacao.setIdEnderecoDeEstoque(request.getIdEnderecoDeEstoque());
        variacao.setIdInformacaoDeRetirada(request.getIdInformacaoDeRetirada());
        
        // Atualiza Características
        List<Caracteristica> novasCars = salvarCaracteristicas(request.getCaracteristicas());
        variacao.setCaracteristicas(novasCars);

        variacao.setDataUltimaModificacao(LocalDateTime.now());
        return variacaoRepository.save(variacao);
    }

    // =========================================================================
    // MOVIMENTAÇÃO DE ESTOQUE (MÉTODO ATÔMICO)
    // =========================================================================
    @Transactional
    public void debitarEstoque(UUID idVariacao, int quantidadeComprada) {
        Variacao variacao = buscarPorId(idVariacao);
        
        if (variacao.getEstoque() == null) {
            throw new RuntimeException("Esta variação não possui estoque configurado.");
        }

        int saldoAtual = variacao.getEstoque().getQtdInicial() - variacao.getEstoque().getQtdVendida();

        if (saldoAtual < quantidadeComprada) {
            throw new RuntimeException("Estoque insuficiente. Saldo atual: " + saldoAtual);
        }

        variacao.getEstoque().setQtdVendida(variacao.getEstoque().getQtdVendida() + quantidadeComprada);
        variacao.setDataUltimaModificacao(LocalDateTime.now());
        
        variacaoRepository.save(variacao);
    }
    
    @Transactional
    public void estornarEstoque(UUID idVariacao, int quantidadeDevolvida) {
        Variacao variacao = buscarPorId(idVariacao);
        
        if (variacao.getEstoque() != null) {
            int novaQtdVendida = variacao.getEstoque().getQtdVendida() - quantidadeDevolvida;
            if (novaQtdVendida < 0) novaQtdVendida = 0;
            
            variacao.getEstoque().setQtdVendida(novaQtdVendida);
            variacao.setDataUltimaModificacao(LocalDateTime.now());
            variacaoRepository.save(variacao);
        }
    }

    // =========================================================================
    // PATCH (Estoque Rápido)
    // =========================================================================
    public Variacao atualizarEstoque(UUID id, Double novoPreco, Integer novaQtd) {
        Variacao variacao = buscarPorId(id);
        
        if (variacao.getEstoque() == null) variacao.setEstoque(new Estoque());
        if (novoPreco != null) variacao.getEstoque().setPreco(novoPreco);
        if (novaQtd != null) variacao.getEstoque().setQtdInicial(novaQtd);
        
        variacao.setDataUltimaModificacao(LocalDateTime.now());
        return variacaoRepository.save(variacao);
    }

    // =========================================================================
    // READ & DELETE
    // =========================================================================
    public void deletarVariacao(UUID id) {
        Variacao variacao = buscarPorId(id);
        variacao.setHabilitado(false);
        variacao.setDataUltimaModificacao(LocalDateTime.now());
        variacaoRepository.save(variacao);
    }

    public Variacao buscarPorId(UUID id) {
        return variacaoRepository.findByIdAndHabilitadoTrue(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Variação não encontrada: " + id));
    }

    public List<Variacao> listarPorProduto(UUID idProduto) {
        return variacaoRepository.findByIdProdutoAndHabilitadoTrue(idProduto);
    }


    // =========================================================================
    // AUXILIARES
    // =========================================================================
    private List<Caracteristica> salvarCaracteristicas(List<VariacaoRequest.CaracteristicaRequest> listaRequest) {
        List<Caracteristica> salvas = new ArrayList<>();
        if (listaRequest == null) return salvas;

        for (VariacaoRequest.CaracteristicaRequest req : listaRequest) {
            TipoDeCaracteristica tipo = new TipoDeCaracteristica();
            tipo.setId(req.getIdTipoCaracteristica());

            Caracteristica c = Caracteristica.builder()
                .idTipo(req.getIdTipoCaracteristica())
                .tipo(tipo)
                .valor(req.getValor())
                .build();
            
            c.setId(UUID.randomUUID());
            c.setHabilitado(true);
            c.setDataCriacao(LocalDateTime.now());
            
            salvas.add(caracteristicaRepository.save(c));
        }
        return salvas;
    }

    private Variacao construirVariacao(VariacaoRequest req) {
        Estoque estoque = Estoque.builder()
            .preco(req.getEstoque().getPreco())
            .qtdInicial(req.getEstoque().getQtdInicial())
            .qtdVendida(0)
            .build();

        Variacao v = new Variacao();
        // v.setSku(req.getSku()); <--- REMOVIDO
        v.setNome(req.getNome());
        v.setDescricao(req.getDescricao());
        v.setEstado(req.getEstado());
        v.setStatus(req.getStatus());
        v.setPeso(req.getPeso());
        v.setTamanhoDeEnvioX(req.getTamanhoDeEnvioX());
        v.setTamanhoDeEnvioY(req.getTamanhoDeEnvioY());
        v.setTamanhoDeEnvioZ(req.getTamanhoDeEnvioZ());
        v.setFotos(req.getFotos());
        v.setEstoque(estoque);
        
        v.setIdEnderecoDeEstoque(req.getIdEnderecoDeEstoque());
        v.setIdInformacaoDeRetirada(req.getIdInformacaoDeRetirada());
        
        return v;
    }
}