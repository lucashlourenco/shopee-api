// src/main/java/br/com/ifpe.shopee.model/bd_principal/controller/VendedorController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Vendedor;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroVendedorRequest;
import br.com.ifpe.shopee.model.bd_principal.service.VendedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vendedores")
@Tag(
    name = "API Vendedor",
    description = "API responsável pelos serviços de vendedor no sistema."
)
public class VendedorController {
    
    @Autowired
    private VendedorService vendedorService;

    @Operation(
        summary = "Serviço responsável por salvar um vendedor no sistema."
    )
    @PostMapping
    public ResponseEntity<Vendedor> adicionarVendedor(@RequestBody @Valid CadastroVendedorRequest request) {
        // O Service orquestra Pessoa, Usuario, Loja e Vendedor
        Vendedor novoVendedor = vendedorService.adicionarVendedor(request);
        return new ResponseEntity<>(novoVendedor, HttpStatus.CREATED);
    }
    
    @Operation(
        summary = "Serviço responsável por buscar um vendedor pelo ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Vendedor> obterVendedorPorID(@PathVariable UUID id) {
        Vendedor vendedor = vendedorService.obterPorID(id);
        return ResponseEntity.ok(vendedor);
    }
    
    @Operation(
        summary = "Serviço responsável por excluir (logicamente) um vendedor pelo ID.",
        description = "Exclusão lógica do Tipo de Usuário Vendedor. Apaga o Papel Vendedor e o Usuário atrelado a ele, se não houver mais outro tipo também apaga a Pessoa."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarVendedor(@PathVariable UUID id) {
        vendedorService.apagarVendedor(id);
        return ResponseEntity.noContent().build();
    }
}