// src/main/java/br/com/ifpe/shopee.model/bd_principal/controller/ClienteController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroClienteRequest;
import br.com.ifpe.shopee.model.bd_principal.service.ClienteService;
import br.com.ifpe.shopee.model.bd_principal.service.PessoaService;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeUsuarioService;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeCadastroService;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeEntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


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
    private PessoaService pessoaService;

    @Autowired
    private ContatoDeUsuarioService contatoService;

    @Autowired
    private EnderecoDeEntregaService enderecoEntregaService;

    @Autowired
    private EnderecoDeCadastroService enderecoDeCadastroService;

    // Gestão de Cliente

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
        summary = "Serviço responsável por excluir (logicamente) um cliente pelo ID.",
        description = "Exclusão lógica do Tipo de Usuário Cliente. Apaga o Papel Cliente, o Usuário atrelado a ele (se for o último papel) e a Pessoa (se for o último Usuário)."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCliente(@PathVariable UUID id) {
        clienteService.apagarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // Gestão de Pessoas

    @Operation(
        summary = "Serviço responsável por buscar os dados pessoais de um cliente pelo seu ID de Cliente."
    )
    @GetMapping("/{idCliente}/pessoa")
    public ResponseEntity<Pessoa> obterPessoa(@PathVariable("idCliente") UUID idCliente) {
        Cliente cliente = clienteService.obterPorID(idCliente);
        return ResponseEntity.ok(cliente.getUsuario().getPessoa());
    }

    @Operation(summary = "Serviço responsável por atualizar os dados pessoais de um cliente pelo seu ID de Cliente.")
    @PatchMapping("/{idCliente}/pessoa")
    public ResponseEntity<Pessoa> atualizarPessoa(@PathVariable("idCliente") UUID idCliente, @RequestBody Pessoa pessoaRequest) {
        Cliente cliente = clienteService.obterPorID(idCliente);
        UUID idPessoaReal = cliente.getUsuario().getPessoa().getId();
        Pessoa pessoaAtualizada = pessoaService.alterarPessoa(idPessoaReal, pessoaRequest);
        return ResponseEntity.ok(pessoaAtualizada);
    }

    // Gestão de EndereçoDeCadastro

    @Operation(
        summary = "Serviço responsável por buscar o endereço de cadastro de um cliente pelo seu ID de Cliente."
    )
    @GetMapping("/{idCliente}/endereco-cadastro")
    public ResponseEntity<EnderecoDeCadastro> obterEnderecoDeCadastro(@PathVariable("idCliente") UUID idCliente) {
        Cliente cliente = clienteService.obterPorID(idCliente);
        EnderecoDeCadastro endereco = cliente.getUsuario().getPessoa().getEndereco();
        return ResponseEntity.ok(endereco);
    }

    @Operation(summary = "Serviço responsável por atualizar o endereço de cadastro de um cliente pelo seu ID de Cliente.")
    @PatchMapping("/{idCliente}/endereco-cadastro")
    public ResponseEntity<EnderecoDeCadastro> atualizarEnderecoDeCadastro(@PathVariable("idCliente") UUID idCliente, @RequestBody EnderecoDeCadastro novoEndereco) {
        Cliente cliente = clienteService.obterPorID(idCliente);
        UUID idPessoa = cliente.getUsuario().getPessoa().getId();
        return ResponseEntity.ok(pessoaService.alterarEnderecoDeCadastro(idPessoa, novoEndereco).getEndereco());
    }
}