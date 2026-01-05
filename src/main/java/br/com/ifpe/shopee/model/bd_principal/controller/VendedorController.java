// src/main/java/br/com/ifpe.shopee.model/bd_principal/controller/VendedorController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.shopee.model.bd_blobstore.entity.Midia;
import br.com.ifpe.shopee.model.bd_blobstore.service.MidiaService;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.Vendedor;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroVendedorRequest;
import br.com.ifpe.shopee.model.bd_principal.request.LojaRequest;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeUsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.request.contato.ContatoDeLojaRequest;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoComercialRequest;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoDeEstoqueRequest;
import br.com.ifpe.shopee.model.bd_principal.service.LojaService;
import br.com.ifpe.shopee.model.bd_principal.service.VendedorService;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeLojaService;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vendedor")
@Tag(
    name = "API Vendedor",
    description = "API responsável pelos serviços de vendedor no sistema."
)
public class VendedorController {
    
    @Autowired
    private VendedorService vendedorService;

    @Autowired
    private LojaService lojaService;

    @Autowired
    private ContatoDeLojaService contatoDeLojaService;

    @Autowired
    private MidiaService midiaService;

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // -------------------------------------------------
    // Gestão de Vendedor
    // -------------------------------------------------

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
        summary = "Serviço responsável por buscar um vendedor pelo seu ID."
    )
    @GetMapping("/{idVendedor}")
    public ResponseEntity<Vendedor> obterVendedorPorID(@PathVariable UUID idVendedor) {
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        return ResponseEntity.ok(vendedor);
    }

    @Operation(
        summary = "Serviço responsável por atualizar dados de um vendedor.",
        description = "O único dado que pode ser atualizado de um vendedor é o nome."
    )
    @PutMapping("/{idVendedor}")
    public ResponseEntity<Vendedor> atualizarVendedor(@PathVariable UUID idVendedor, @RequestBody @Valid TipoDeUsuarioRequest request) {
        Usuario usuarioLogado = getUsuarioLogado();
        Vendedor vendedor = vendedorService.atualizarVendedor(idVendedor, request, usuarioLogado);
        return ResponseEntity.ok(vendedor);
    }

    @Operation(
        summary = "Serviço responsável por excluir (logicamente) um vendedor pelo seu ID.",
        description = "Exclusão lógica do Tipo de Usuário Vendedor. Apaga o Papel Vendedor e a Loja, se não houver mais outro papel ativo, também apaga o Usuário e a Pessoa."
    )
    @DeleteMapping("/{idVendedor}")
    public ResponseEntity<Void> apagarVendedor(@PathVariable UUID idVendedor) {
        Usuario usuarioLogado = getUsuarioLogado();
        vendedorService.apagarVendedor(idVendedor, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------
    // Gestão de Loja
    // -------------------------------------------------

    @Operation(summary = "Busca a Loja de um vendedor.")
    @GetMapping("/{idVendedor}/loja")
    public ResponseEntity<Loja> obterLojaDeVendedor(@PathVariable UUID idVendedor) {
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        return ResponseEntity.ok(loja);
    }

    @Operation(summary = "Atualiza dados da Loja (Nome, Descrição, Logo).")
    @PutMapping("/{idVendedor}/loja")
    public ResponseEntity<Loja> atualizarLoja(
            @PathVariable UUID idVendedor,
            @RequestBody @Valid LojaRequest request) {
        
        // 1. Verifica se o vendedor existe e pega a loja dele
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        
        // Se não existir a loja do vendedor...
        if (loja == null) {
             throw new RecursoNaoEncontradoException("Este vendedor não possui loja ativa.");
        }

        // Recupera o usuário logado
        Usuario usuarioLogado = getUsuarioLogado();
        // 2. Faz a atualização e retorna
        Loja lojaAtualizada = lojaService.atualizarLoja(loja.getId(), request.build(), usuarioLogado);
        
        return ResponseEntity.ok(lojaAtualizada);
    }

    // -------------------------------------------------
    // Gestão de Contatos da Loja
    // -------------------------------------------------

    @Operation(summary = "Adiciona um contato à loja de um vendedor.")
    @PostMapping("/{idVendedor}/loja/contato")
    public ResponseEntity<ContatoDeLoja> adicionarContatoDeLoja(
            @PathVariable UUID idVendedor,
            @RequestBody @Valid ContatoDeLojaRequest request) {
        
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        Usuario usuarioLogado = getUsuarioLogado();
        ContatoDeLoja novoContato = contatoDeLojaService.adicionarContatoDeLoja(loja.getId(), request.build(), usuarioLogado);

        return new ResponseEntity<>(novoContato, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os contatos da loja de um vendedor.")
    @GetMapping("/{idVendedor}/loja/contatos")
    public ResponseEntity<List<ContatoDeLoja>> obterTodosOsContatosDeLoja(@PathVariable UUID idVendedor) {
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        List<ContatoDeLoja> contatos = contatoDeLojaService.listarContatosDeLoja(loja.getId());
        return ResponseEntity.ok(contatos);
    }

    @Operation(summary = "Remove um contato da loja de um vendedor.")
    @DeleteMapping("/{idVendedor}/loja/contato/{idContato}")
    public ResponseEntity<Void> removerContatoDeLoja(
            @PathVariable UUID idVendedor,
            @PathVariable UUID idContato) {
        
        Usuario usuarioLogado = getUsuarioLogado();
        contatoDeLojaService.apagarContatoDeLoja(idContato, usuarioLogado);
        
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------
    // Gestão de Endereço Comercial
    // -------------------------------------------------

    @Operation(summary = "Atualiza o endereço comercial da loja.")
    @PutMapping("/{idVendedor}/loja/endereco-comercial")
    public ResponseEntity<EnderecoComercial> atualizarEnderecoComercial(
            @PathVariable UUID idVendedor,
            @RequestBody @Valid EnderecoComercialRequest request) {
        
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        
        if (loja == null) {
             throw new RecursoNaoEncontradoException("Loja não encontrada.");
        }

        Usuario usuarioLogado = getUsuarioLogado();
        Loja lojaAtualizada = lojaService.atualizarEnderecoComercial(loja.getId(), request.build(), usuarioLogado);

        return ResponseEntity.ok(lojaAtualizada.getEndereco());
    }

    // -------------------------------------------------
    // Gestão de Endereço de Estoque
    // -------------------------------------------------

    @Operation(summary = "Adiciona um novo depósito/estoque à loja.")
    @PostMapping("/{idVendedor}/loja/estoque")
    public ResponseEntity<EnderecoDeEstoque> adicionarDeposito(
            @PathVariable UUID idVendedor,
            @RequestBody @Valid EnderecoDeEstoqueRequest request) {
        
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();

        Usuario usuarioLogado = getUsuarioLogado();
        // O Service cuida de encontrar/criar e vincular
        EnderecoDeEstoque estoque = lojaService.adicionarEnderecoDeEstoque(loja.getId(), request.build(), usuarioLogado);
        
        return ResponseEntity.ok(estoque);
    }

    @Operation(summary = "Lista todos os depositos vinculados à loja.")
    @GetMapping("/{idVendedor}/loja/estoque")
    public ResponseEntity<List<EnderecoDeEstoque>> listarDepositos(@PathVariable UUID idVendedor) {
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        Loja loja = vendedor.getLoja();
        
        if (loja == null) {
             throw new RecursoNaoEncontradoException("Loja não encontrada.");
        }
        List<EnderecoDeEstoque> depositos = lojaService.listarEstoques(loja.getId());
        
        return ResponseEntity.ok(depositos);
    }

    @Operation(summary = "Remove um vínculo de estoque da loja.")
    @DeleteMapping("/{idVendedor}/loja/estoque/{idEstoque}")
    public ResponseEntity<Void> removerDeposito(
            @PathVariable UUID idVendedor,
            @PathVariable UUID idEstoque) {
        
        Vendedor vendedor = vendedorService.obterPorID(idVendedor);
        
        Usuario usuarioLogado = getUsuarioLogado();
        // O Service cuida de desvincular e limpar órfãos
        lojaService.removerEnderecoDeEstoque(vendedor.getLoja().getId(), idEstoque, usuarioLogado);

        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------
    // Upload de Arquivos
    // -------------------------------------------------

    @Operation(summary = "Realiza upload de uma imagem (Logo, Produto). Retorna o objeto Midia criado.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Midia> uploadMidia(
            @RequestParam("arquivo") MultipartFile arquivo, 
            @RequestParam("origem") String origem // Ex: "Loja", "Produto"
            ) {
        
        // Pegando o usuário do Token
        Usuario usuario = getUsuarioLogado();
        Midia midia = midiaService.uploadMidia(arquivo, usuario, origem);
        
        return new ResponseEntity<>(midia, HttpStatus.CREATED);
    }
}