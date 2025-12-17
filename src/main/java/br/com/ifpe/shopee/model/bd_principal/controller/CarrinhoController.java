package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;
import br.com.ifpe.shopee.model.bd_principal.service.CarrinhoService;
import lombok.Data;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    // Buscar Carrinho do Cliente
    @GetMapping("/{idCliente}")
    public ResponseEntity<CarrinhoDeCompra> buscarCarrinho(@PathVariable UUID idCliente) {
        return ResponseEntity.ok(carrinhoService.buscarCarrinhoDoCliente(idCliente));
    }

    // Adicionar Item
    @PostMapping("/item")
    public ResponseEntity<CarrinhoDeCompra> adicionarItem(@RequestBody ItemRequest request) {
        CarrinhoDeCompra carrinho = carrinhoService.adicionarItem(
            request.getIdCliente(), 
            request.getIdVariacao(), 
            request.getQuantidade()
        );
        return ResponseEntity.ok(carrinho);
    }

    // Remover Item
    @DeleteMapping("/item/{idItem}")
    public ResponseEntity<Void> removerItem(@PathVariable UUID idItem) {
        carrinhoService.removerItem(idItem);
        return ResponseEntity.noContent().build();
    }

    // DTO auxiliar simples
    @Data
    public static class ItemRequest {
        private UUID idCliente;
        private UUID idVariacao;
        private int quantidade;
    }
}