// src/main/java/br/com/ifpe/shopee.model/bd_principal/controller/ClienteController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroClienteRequest;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeUsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoDeEntregaRequest;
import br.com.ifpe.shopee.model.bd_principal.service.ClienteService;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeEntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/cliente")
@Tag(
    name = "API Cliente",
    description = "API responsável pelos serviços de cliente no sistema."
)
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EnderecoDeEntregaService enderecoDeEntregaService;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // -------------------------------------------------
    // Gestão de Cliente
    // -------------------------------------------------

    @Operation(
        summary = "Serviço responsável por salvar um cliente no sistema.",
        description = "Orquestra a criação completa de um novo Cliente (Pessoa, Usuario, Cliente e Endereços - enderecoDeCadastro e, se houver, enderecoDeEntrega)."
    )
    @PostMapping
    public ResponseEntity<Cliente> adicionarCliente(@RequestBody @Valid CadastroClienteRequest request) {
        Cliente novoCliente = clienteService.adicionarCliente(request);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Serviço responsável por buscar um cliente pelo ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable UUID id) {
        Cliente cliente = clienteService.obterPorID(id);
        return ResponseEntity.ok(cliente);
    }

    @Operation(
        summary = "Serviço responsável por atualizar um cliente pelo ID.",
        description = "O único dado que pode ser atualizado de um cliente é o nome."
    )

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable UUID id, @RequestBody @Valid TipoDeUsuarioRequest request) {
        Usuario usuarioLogado = getUsuarioLogado();
        Cliente cliente = clienteService.atualizarCliente(id, request, usuarioLogado);
        return ResponseEntity.ok(cliente);
    }
    
    @Operation(
        summary = "Serviço responsável por excluir (logicamente) um cliente pelo ID.",
        description = "Exclusão lógica do Tipo de Usuário Cliente. Apaga o Papel Cliente e, se for o último papel ativo, apaga o Usuário e a Pessoa."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCliente(@PathVariable UUID id) {
        Usuario usuarioLogado = getUsuarioLogado();
        clienteService.apagarCliente(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------
    // Gestão de Endereços de Entrega
    // -------------------------------------------------

    @Operation(summary = "Adiciona um novo endereço de entrega.")
    @PostMapping("/{idCliente}/endereco-entrega")
    public ResponseEntity<EnderecoDeEntrega> adicionarEnderecoEntrega(
            @PathVariable UUID idCliente,
            @RequestBody @Valid EnderecoDeEntregaRequest request) {
        
        EnderecoDeEntrega novoEndereco = request.build();
        Usuario usuarioLogado = getUsuarioLogado();
        EnderecoDeEntrega enderecoSalvo = enderecoDeEntregaService.adicionarEnderecoDeEntrega(idCliente, novoEndereco, usuarioLogado);
        return new ResponseEntity<>(enderecoSalvo, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os endereços de entrega do cliente.")
    @GetMapping("/{idCliente}/endereco-entrega")
    public ResponseEntity<List<EnderecoDeEntrega>> listarTodosEnderecosEntrega(@PathVariable UUID idCliente) {
        Usuario usuarioLogado = getUsuarioLogado();
        List<EnderecoDeEntrega> enderecos = enderecoDeEntregaService.listarPorCliente(idCliente, usuarioLogado);
        return ResponseEntity.ok(enderecos);
    }

    @Operation(summary = "Atualiza um endereço de entrega específico.")
    @PutMapping("/{idCliente}/endereco-entrega/{idEndereco}")
    public ResponseEntity<EnderecoDeEntrega> atualizarEnderecoEntrega(
            @PathVariable UUID idCliente,
            @PathVariable UUID idEndereco,
            @RequestBody @Valid EnderecoDeEntregaRequest request) {

        Usuario usuarioLogado = getUsuarioLogado();
        EnderecoDeEntrega enderecoAtualizado = enderecoDeEntregaService.alterarEndereco(idEndereco, request.build(), usuarioLogado);

        return ResponseEntity.ok(enderecoAtualizado);
    }

    @Operation(summary = "Remove um endereço de entrega.")
    @DeleteMapping("/{idCliente}/endereco-entrega/{idEndereco}")
    public ResponseEntity<Void> removerEnderecoEntrega(
            @PathVariable UUID idCliente,
            @PathVariable UUID idEndereco) {

        Usuario usuarioLogado = getUsuarioLogado();
        enderecoDeEntregaService.apagarEndereco(idEndereco, usuarioLogado);
        
        return ResponseEntity.noContent().build();
    }
}