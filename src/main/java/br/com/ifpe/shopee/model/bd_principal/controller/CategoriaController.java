package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Categoria;
import br.com.ifpe.shopee.model.bd_principal.request.CategoriaRequest;
import br.com.ifpe.shopee.model.bd_principal.response.CategoriaTreeResponse; // Import do DTO de Árvore
import br.com.ifpe.shopee.model.bd_principal.service.CategoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<Categoria> criar(@RequestBody @Valid CategoriaRequest request) {
        Categoria nova = categoriaService.criarCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable UUID id, @RequestBody @Valid CategoriaRequest request) {
        Categoria atualizada = categoriaService.atualizarCategoria(id, request);
        return ResponseEntity.ok(atualizada);
    }

    // --- DELETE (Lógico) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    // --- READ (Endpoints de Navegação) ---

    // 1. Busca Simples por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // 2. Árvore Completa (Menu Principal - Pai + Filhas aninhadas)
    @GetMapping("/arvore")
    public ResponseEntity<List<CategoriaTreeResponse>> listarArvore() {
        List<CategoriaTreeResponse> arvore = categoriaService.listarArvoreCompleta();
        return ResponseEntity.ok(arvore);
    }

    // 3. Listar apenas as Raízes (Categorias Principais)
    @GetMapping("/raizes")
    public ResponseEntity<List<Categoria>> listarRaizes() {
        return ResponseEntity.ok(categoriaService.listarRaizes());
    }

    // 4. Listar as Filhas diretas de uma Categoria (Drill-down)
    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<List<Categoria>> listarFilhas(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.listarFilhas(id));
    }
}