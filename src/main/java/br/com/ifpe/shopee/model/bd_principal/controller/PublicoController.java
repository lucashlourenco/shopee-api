// src/main/java/br/com/ifpe.shopee.model/bd_principal/controller/PublicoController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.request.LoginRequest;
import br.com.ifpe.shopee.model.bd_principal.service.LojaService;
import br.com.ifpe.shopee.model.bd_principal.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/publico")
@Tag(
    name = "API de Acesso Público",
    description = "API responsável pela autenticação e exibição de dados publicamente (Lojas, Produtos, etc)."
)
public class PublicoController {

    @Autowired
    private LojaService lojaService;

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Serviço de Autenticação/Login.",
        description = "Autentica o usuário com login (email/telefone) e senha."
    )
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody @Valid LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.getLogin(), request.getSenha());
        return ResponseEntity.ok(usuario);
    }
    
    @Operation(
        summary = "Serviço de exibição de Loja publicamente.",
        description = "Retorna uma Loja solicitada pelo ID, se estiver ativa (habilitada)."
    )
    @GetMapping("/loja/{lojaId}")
    public ResponseEntity<Loja> visitarLoja(
        @PathVariable UUID lojaId
    ) {
        Loja loja = lojaService.obterPorID(lojaId);
        return ResponseEntity.ok(loja);
    }
}