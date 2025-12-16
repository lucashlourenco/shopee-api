package br.com.ifpe.shopee.model.bd_secundario.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.model.bd_secundario.request.ProdutoRequest;
import br.com.ifpe.shopee.model.bd_secundario.service.ProdutoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody @Valid ProdutoRequest request) {
        // ID fixo simulado
        UUID idLojaDoVendedor = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); 
        Produto produtoSalvo = produtoService.criarProduto(request, idLojaDoVendedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    // --- UPDATE (PUT) --- 
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable UUID id, @RequestBody @Valid ProdutoRequest request) {
        // ID fixo simulado
        UUID idLojaDoVendedor = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Produto produtoAtualizado = produtoService.atualizarProduto(id, request, idLojaDoVendedor);
        return ResponseEntity.ok(produtoAtualizado);
    }

    // --- DELETE (Lógico) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        // ID fixo simulado
        UUID idLojaDoVendedor = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        produtoService.deletarProduto(id, idLojaDoVendedor);
        return ResponseEntity.noContent().build();
    }

    // --- READ ---

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable UUID id) {
        Produto produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/loja/{idLoja}")
    public ResponseEntity<List<Produto>> listarPorLoja(@PathVariable UUID idLoja) {
        List<Produto> produtos = produtoService.listarProdutosPorLoja(idLoja);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Produto>> listarPorCategoria(@PathVariable UUID idCategoria) {
        List<Produto> produtos = produtoService.listarProdutosPorCategoria(idCategoria);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<List<Produto>> pesquisar(@RequestParam String termo) {
        List<Produto> produtos = produtoService.pesquisarProdutos(termo);
        return ResponseEntity.ok(produtos);
    }
}