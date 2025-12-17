package br.com.ifpe.shopee.model.bd_secundario.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_secundario.entity.Pagamento;
import br.com.ifpe.shopee.model.bd_secundario.request.PagamentoRequest;
import br.com.ifpe.shopee.model.bd_secundario.service.PagamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    // Iniciar Pagamento
    @PostMapping
    public ResponseEntity<Pagamento> criar(@RequestBody @Valid PagamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPagamento(request));
    }

    // --- AÇÕES DO USUÁRIO ---
    
    // Cancelar (Desistência para tentar outro método)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Pagamento> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelarPagamentoPeloUsuario(id));
    }

    // --- WEBHOOKS ---
    
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Pagamento> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirmarPagamento(id));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Pagamento> recusar(@PathVariable UUID id, @RequestParam String motivo) {
        return ResponseEntity.ok(service.recusarPagamento(id, motivo));
    }
    
    // Endpoint auxiliar para testar busca por ID externo (Webhook Real)
    @GetMapping("/transacao/{idTransacao}")
    public ResponseEntity<Pagamento> buscarPorTransacao(@PathVariable String idTransacao) {
        return ResponseEntity.ok(service.buscarPorTransacaoExterna(idTransacao));
    }

    // --- READ ---
    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<Pagamento> buscarPorPedido(@PathVariable UUID idPedido) {
        return ResponseEntity.ok(service.buscarPorPedido(idPedido));
    }
}