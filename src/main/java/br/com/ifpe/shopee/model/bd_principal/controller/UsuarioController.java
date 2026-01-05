// src/main/java/br/com/ifpe/shopee/model/bd_principal/controller/UsuarioController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.request.PessoaRequest;
import br.com.ifpe.shopee.model.bd_principal.request.UsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.request.contato.ContatoDeLoginRequest;
import br.com.ifpe.shopee.model.bd_principal.request.contato.ContatoDeUsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoDeCadastroRequest;
import br.com.ifpe.shopee.model.bd_principal.service.PessoaService;
import br.com.ifpe.shopee.model.bd_principal.service.UsuarioService;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeLoginService;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(
    name = "API Usuário (Minha Conta)",
    description = "API responsável pela gestão da conta, dados pessoais, endereço de cadastro e contatos."
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ContatoDeUsuarioService contatoDeUsuarioService;

    @Autowired
    private ContatoDeLoginService contatoDeLoginService;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // -------------------------------------------------
    // Gestão da Conta (Usuario)
    // -------------------------------------------------

    @Operation(summary = "Obtém os dados completos do usuário (incluindo Pessoa e Endereço de Cadastro).")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obterUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obterPorID(id));
    }

    @Operation(summary = "Altera a senha do usuário.")
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable UUID id, @RequestBody @Valid UsuarioRequest request) {
        Usuario usuarioLogado = getUsuarioLogado();
        usuarioService.alterarSenha(id, request.getSenha(), usuarioLogado);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------------------
    // Gestão das credenciais (Contato de Login)
    // -------------------------------------------------

    @Operation(summary = "Possibilita adicionar uma credencial extra (email/telefone) para login até o máximo (2).")
    @PostMapping("/{id}/credencial")
    public ResponseEntity<ContatoDeLogin> adicionarCredencial(
            @PathVariable UUID id, 
            @RequestBody @Valid ContatoDeLoginRequest request) {
        
        ContatoDeLogin novaCredencial = request.build();
        Usuario usuarioLogado = getUsuarioLogado();
        ContatoDeLogin salvo = contatoDeLoginService.adicionarCredencial(id, novaCredencial, usuarioLogado);
        
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Remove uma credencial de login até o mínimo (1).",
        description = "Para apagar a conta deve-se apagar Cliente/Vendedor."
    )
    @DeleteMapping("/{id}/credencial/{idCredencial}")
    public ResponseEntity<Void> removerCredencial(
            @PathVariable UUID id, 
            @PathVariable UUID idCredencial) {
        
        Usuario usuarioLogado = getUsuarioLogado();
        contatoDeLoginService.apagarCredencial(id, idCredencial, usuarioLogado);
        
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------
    // Gestão de Dados Pessoais (Pessoa)
    // -------------------------------------------------

    @Operation(summary = "Obtém apenas os dados pessoais do usuário.")
    @GetMapping("/{id}/pessoa")
    public ResponseEntity<Pessoa> obterDadosPessoais(@PathVariable UUID id) {
        Usuario usuario = usuarioService.obterPorID(id);
        return ResponseEntity.ok(usuario.getPessoa());
    }

    @Operation(summary = "Atualiza os dados pessoais (nome, nacionalidade e data de nascimento).")
    @PutMapping("/{id}/pessoa")
    public ResponseEntity<Pessoa> atualizarDadosPessoais(@PathVariable UUID id, @RequestBody @Valid PessoaRequest request) {
        Usuario usuario = usuarioService.obterPorID(id);
        Usuario usuarioLogado = getUsuarioLogado();
        Pessoa pessoaAtualizada = pessoaService.alterarPessoa(usuario.getPessoa().getId(), request.build(), usuarioLogado);
        return ResponseEntity.ok(pessoaAtualizada);
    }

    // -------------------------------------------------
    // Gestão de Endereço de Cadastro (Residencial)
    // -------------------------------------------------

    @Operation(summary = "Obtém apenas o endereço de cadastro do usuário.")
    @GetMapping("/{id}/endereco-de-cadastro")
    public ResponseEntity<EnderecoDeCadastro> obterEnderecoCadastro(@PathVariable UUID id) {
        Usuario usuario = usuarioService.obterPorID(id);
        return ResponseEntity.ok(usuario.getPessoa().getEndereco());
    }

    @Operation(summary = "Atualiza o endereço de cadastro da pessoa.")
    @PutMapping("/{id}/endereco-de-cadastro")
    public ResponseEntity<EnderecoDeCadastro> atualizarEnderecoCadastro(
            @PathVariable UUID id, 
            @RequestBody @Valid EnderecoDeCadastroRequest request) {
        
        Usuario usuario = usuarioService.obterPorID(id);
        Usuario usuarioLogado = getUsuarioLogado();
        // O service de Pessoa já tem um método que orquestra a troca segura de endereço
        Pessoa pessoa = pessoaService.alterarEndereco(usuario.getPessoa().getId(), request.build(), usuarioLogado);
        
        return ResponseEntity.ok(pessoa.getEndereco());
    }

    // -------------------------------------------------
    // Gestão de Contato de Usuário
    // -------------------------------------------------

    @Operation(summary = "Adiciona um novo contato ao perfil do usuário.")
    @PostMapping("/{id}/contato")
    public ResponseEntity<ContatoDeUsuario> adicionarContato(@PathVariable UUID id, @RequestBody @Valid ContatoDeUsuarioRequest request) {
        ContatoDeUsuario novoContato = request.build();
        Usuario usuarioLogado = getUsuarioLogado();
        ContatoDeUsuario contatoSalvo = contatoDeUsuarioService.adicionarContatoDeUsuario(id, novoContato, usuarioLogado);
        return new ResponseEntity<>(contatoSalvo, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os contatos do usuário.")
    @GetMapping("/{id}/contatos")
    public ResponseEntity<List<ContatoDeUsuario>> listarContatos(@PathVariable UUID id) {
        return ResponseEntity.ok(contatoDeUsuarioService.listarContatosDeUsuario(id));
    }

    @Operation(summary = "Altera um contato do usuário.")
    @PutMapping("/contato/{idContato}")
    public ResponseEntity<ContatoDeUsuario> atualizarContato(@PathVariable UUID idContato, @RequestBody @Valid ContatoDeUsuarioRequest request) {
        Usuario usuarioLogado = getUsuarioLogado();
        ContatoDeUsuario contatoAtualizado = contatoDeUsuarioService.alterarContatoDeUsuario(idContato, request.build(), usuarioLogado);
        return ResponseEntity.ok(contatoAtualizado);
    }

    @Operation(summary = "Remove um contato do usuário.")
    @DeleteMapping("/contato/{idContato}")
    public ResponseEntity<Void> removerContato(@PathVariable UUID idContato) {
        Usuario usuarioLogado = getUsuarioLogado();
        contatoDeUsuarioService.apagarContatoDeUsuario(idContato, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}