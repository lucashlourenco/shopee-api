package br.com.ifpe.shopee.model.bd_secundario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_secundario.entity.Pagamento;
import br.com.ifpe.shopee.model.bd_secundario.entity.RespostaDePagamento;
import br.com.ifpe.shopee.model.bd_secundario.entity.StatusDePagamento;
import br.com.ifpe.shopee.model.bd_secundario.repository.PagamentoRepository;
import br.com.ifpe.shopee.model.bd_secundario.request.PagamentoRequest;
import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    // --- CREATE ---
    public Pagamento criarPagamento(PagamentoRequest request) {
        
        // 1. ROBUSTEZ: Verifica se já existe pagamento "TRAVANDO" o pedido.
        // Se o anterior foi RECUSADO ou CANCELADO, podemos criar um novo.
        // Se estiver EM_ANALISE ou CONFIRMADO, bloqueia.
        Optional<Pagamento> anterior = repository.findByIdPedidoAndHabilitadoTrue(request.getIdPedido());
        
        if (anterior.isPresent()) {
            StatusDePagamentoEnum st = anterior.get().getStatusAtual();
            if (st == StatusDePagamentoEnum.EM_ANALISE || st == StatusDePagamentoEnum.CONFIRMADO) {
                throw new EntidadeDuplicadaException("Existe um pagamento em andamento (" + st + "). Cancele-o para tentar novamente.");
            }
        }

        // Mock Gateway
        RespostaDePagamento detalhes = RespostaDePagamento.builder()
            .valor(request.getDetalhes().getValor())
            .formaDePagamento(request.getDetalhes().getFormaDePagamento())
            .nDeVezes(request.getDetalhes().getParcelas())
            .idTransacaoExterna(UUID.randomUUID().toString())
            .build();

        Pagamento pagamento = new Pagamento();
        pagamento.setIdPedido(request.getIdPedido());
        pagamento.setDetalhes(detalhes);
        pagamento.setObservacaoGeral(request.getObservacao());
        
        adicionarStatusAoHistorico(pagamento, StatusDePagamentoEnum.EM_ANALISE, "Pagamento iniciado.");

        pagamento.gerarId();
        if (pagamento.getId() == null) pagamento.setId(UUID.randomUUID());
        pagamento.setHabilitado(true);
        pagamento.setDataCriacao(LocalDateTime.now());

        return repository.save(pagamento);
    }

    // --- AÇÕES DO USUÁRIO ---

    // NOVO: Permite ao usuário desistir do pagamento atual para tentar outro método
    public Pagamento cancelarPagamentoPeloUsuario(UUID id) {
        Pagamento pagamento = buscarPorId(id);
        
        // Só pode cancelar se ainda estiver em análise
        if (pagamento.getStatusAtual() != StatusDePagamentoEnum.EM_ANALISE) {
            throw new IllegalArgumentException("Não é possível cancelar um pagamento que já foi processado (" + pagamento.getStatusAtual() + ")");
        }

        adicionarStatusAoHistorico(pagamento, StatusDePagamentoEnum.CANCELADO, "Cancelado pelo usuário (desistência/alteração).");
        pagamento.setDataUltimaModificacao(LocalDateTime.now());
        
        return repository.save(pagamento);
    }

    // --- WEBHOOKS (MÁQUINA DE ESTADOS) ---
    
    public Pagamento confirmarPagamento(UUID id) {
        Pagamento pagamento = buscarPorId(id);
        validarTransicao(pagamento, StatusDePagamentoEnum.CONFIRMADO);
        adicionarStatusAoHistorico(pagamento, StatusDePagamentoEnum.CONFIRMADO, "Aprovado pelo Gateway.");
        pagamento.setDataUltimaModificacao(LocalDateTime.now());
        return repository.save(pagamento);
    }

    public Pagamento recusarPagamento(UUID id, String motivo) {
        Pagamento pagamento = buscarPorId(id);
        validarTransicao(pagamento, StatusDePagamentoEnum.RECUSADO);
        adicionarStatusAoHistorico(pagamento, StatusDePagamentoEnum.RECUSADO, motivo);
        pagamento.setDataUltimaModificacao(LocalDateTime.now());
        return repository.save(pagamento);
    }
    
    // NOVO: Método para o Webhook achar o pagamento pelo ID do Gateway
    public Pagamento buscarPorTransacaoExterna(String idTransacao) {
        return repository.findByDetalhes_IdTransacaoExterna(idTransacao)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Transação externa não encontrada: " + idTransacao));
    }

    // --- READ ---
    public Pagamento buscarPorId(UUID id) {
        return repository.findByIdAndHabilitadoTrue(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado: " + id));
    }
    
    public Pagamento buscarPorPedido(UUID idPedido) {
        return repository.findByIdPedidoAndHabilitadoTrue(idPedido)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado para o pedido: " + idPedido));
    }

    // --- AUXILIARES ---
    private void adicionarStatusAoHistorico(Pagamento pagamento, StatusDePagamentoEnum novoStatus, String obs) {
        if (pagamento.getHistoricoStatus() == null) pagamento.setHistoricoStatus(new ArrayList<>());
        
        StatusDePagamento statusLog = StatusDePagamento.builder()
            .status(novoStatus)
            .dataDeRegistro(LocalDateTime.now())
            .observacao(obs)
            .build();
            
        pagamento.getHistoricoStatus().add(statusLog);
        pagamento.setStatusAtual(novoStatus);
    }

    private void validarTransicao(Pagamento atual, StatusDePagamentoEnum destino) {
        StatusDePagamentoEnum origem = atual.getStatusAtual();
        if (origem == StatusDePagamentoEnum.RECUSADO || 
            origem == StatusDePagamentoEnum.CANCELADO || 
            origem == StatusDePagamentoEnum.CONFIRMADO) {
            throw new IllegalArgumentException("Pagamento já finalizado (" + origem + ").");
        }
    }
}