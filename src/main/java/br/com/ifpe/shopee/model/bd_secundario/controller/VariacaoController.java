package br.com.ifpe.shopee.model.bd_secundario.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.model.bd_secundario.request.VariacaoRequest;
import br.com.ifpe.shopee.model.bd_secundario.service.VariacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/variacoes")
public class VariacaoController {

    @Autowired
    private VariacaoService variacaoService;

    // --- CREATE (Adicionar a um produto) ---
    @PostMapping
    public ResponseEntity<Variacao> criar(@RequestBody @Valid VariacaoRequest request) {
        Variacao nova = variacaoService.adicionarVariacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // --- UPDATE (Completo) ---
    @PutMapping("/{id}")
    public ResponseEntity<Variacao> atualizar(@PathVariable UUID id, @RequestBody @Valid VariacaoRequest request) {
        Variacao atualizada = variacaoService.atualizarVariacao(id, request);
        return ResponseEntity.ok(atualizada);
    }

    // --- PATCH (Ajuste rápido de estoque/preço) ---
    // Ex: PATCH /api/variacoes/{id}/estoque?preco=99.90&qtd=50
    @PatchMapping("/{id}/estoque")
    public ResponseEntity<Variacao> atualizarEstoque(
            @PathVariable UUID id, 
            @RequestParam(required = false) Double preco,
            @RequestParam(required = false) Integer qtd) {
        
        Variacao atualizada = variacaoService.atualizarEstoque(id, preco, qtd);
        return ResponseEntity.ok(atualizada);
    }

    // --- DELETE (Lógico) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        variacaoService.deletarVariacao(id);
        return ResponseEntity.noContent().build();
    }

    // --- READ ---
    @GetMapping("/{id}")
    public ResponseEntity<Variacao> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(variacaoService.buscarPorId(id));
    }

    // Listar todas as variações de um Produto específico
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<Variacao>> listarPorProduto(@PathVariable UUID idProduto) {
        return ResponseEntity.ok(variacaoService.listarPorProduto(idProduto));
    }
}