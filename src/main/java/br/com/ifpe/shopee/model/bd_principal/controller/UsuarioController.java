// src/main/java/br/com/ifpe.shopee.model/bd_principal/controller/UsuarioController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.request.UsuarioRequest; 
import br.com.ifpe.shopee.model.bd_principal.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(
    name = "API Usuário (Conta)",
    description = "API responsável pela autenticação, login e gestão de conta (senha/perfil de acesso)."
)
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Serviço de Alteração de Senha.",
        description = "Altera a senha do usuário logado. Requer o ID do Usuário e o novo campo de senha dentro do DTO."
    )
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
        @PathVariable UUID id, 
        @RequestBody @Valid UsuarioRequest request
    ) {
        usuarioService.alterarSenha(id, request.getSenha());
        return ResponseEntity.ok().build();
    }
}