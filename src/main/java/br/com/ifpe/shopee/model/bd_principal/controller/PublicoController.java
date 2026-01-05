// src/main/java/br/com/ifpe.shopee.model/bd_principal/controller/PublicoController.java

package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_blobstore.service.MidiaService;
import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.Vendedor;
import br.com.ifpe.shopee.model.bd_principal.request.UsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.service.LojaService;
import br.com.ifpe.shopee.model.bd_principal.service.UsuarioService;
import br.com.ifpe.shopee.util.seguranca.JwtService;
import br.com.ifpe.shopee.util.seguranca.TokenResponse;
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

    @Autowired
    private MidiaService midiaService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // -------------------------------------------------
    // Acesso Público a login
    // -------------------------------------------------

    @Operation(
        summary = "Serviço de Autenticação/Login.",
        description = "Autentica o usuário com login (email/telefone) e senha, retornando o Token JWT."
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid UsuarioRequest request) {
        
        // 1. O AuthenticationManager valida usuario e senha (criptografada) automaticamente
        // Se a senha estiver errada, ele lança uma exceção (403/401)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getLogin().getValor(), // O login (email/telefone)
                request.getSenha()
            )
        );

        // 2. Se passou da linha acima, a senha está correta. Buscamos o usuário completo.
        Usuario usuario = usuarioService.buscarUsuarioPorCredencial(request.getLogin().getValor());

        // 3. Monta um payload extra para o Token
        Map<String, Object> extraClaims = new HashMap<>();

        // Dados do Usuário Base
        extraClaims.put("idUsuario", usuario.getId());
        // Transformando as Roles (GrantedAuthority) em lista de Strings
        extraClaims.put("roles", usuario.getAuthorities().stream()
                                        .map(a -> a.getAuthority())
                                        .collect(Collectors.toList()));

        // Dados Específicos (Varrendo os tipos)
        usuario.getTipos().forEach(tipo -> {
            
            if (tipo instanceof Cliente) {
                Cliente cliente = (Cliente) tipo;
                Map<String, Object> dadosCliente = new HashMap<>();
                dadosCliente.put("idCliente", cliente.getId());
                
                // Pega o nome do perfil (ex: "Beto"). Se nulo, pega da Pessoa (ex: "Roberto")
                String nomeExibicao = cliente.getNome() != null ? cliente.getNome() : usuario.getPessoa().getNomeCompleto();
                dadosCliente.put("nome", nomeExibicao);
                
                extraClaims.put("cliente", dadosCliente);
            }
            
            else if (tipo instanceof Vendedor) {
                Vendedor vendedor = (Vendedor) tipo;
                Map<String, Object> dadosVendedor = new HashMap<>();
                dadosVendedor.put("idVendedor", vendedor.getId());
                
                // Lógica de nome para o vendedor também
                String nomeExibicao = vendedor.getNome() != null ? vendedor.getNome() : usuario.getPessoa().getNomeCompleto();
                dadosVendedor.put("nome", nomeExibicao);
                
                dadosVendedor.put("cnpj", vendedor.getCnpj());
                
                if (vendedor.getLoja() != null) {
                    dadosVendedor.put("idLoja", vendedor.getLoja().getId());
                    dadosVendedor.put("nomeLoja", vendedor.getLoja().getNome());
                    dadosVendedor.put("logoLoja", vendedor.getLoja().getLogo());
                }
                
                extraClaims.put("vendedor", dadosVendedor);
            }
        });

        // 4. Gera o Token com os dados extras
        String jwtToken = jwtService.generateToken(extraClaims, usuario);

        // 5. Retorna
        TokenResponse response = TokenResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(response);
    }
    
    // -------------------------------------------------
    // Acesso Público a Lojas
    // -------------------------------------------------

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

    // -------------------------------------------------
    // Acesso Público a Mídias (Imagens/Arquivos)
    // -------------------------------------------------

    @Operation(summary = "Exibe/Baixa uma mídia (imagem/arquivo) pelo ID.")
    @GetMapping("/midia/{id}")
    public ResponseEntity<Resource> carregarMidia(@PathVariable UUID id) {
        // 1. Busca os metadados para saber o Content-Type (image/jpeg, etc),
        // que é importante para o navegador exibir a imagem em vez de baixar
        var midia = midiaService.obterPorId(id);
        
        // 2. Busca o arquivo físico
        Resource arquivo = midiaService.carregarMidiaPeloId(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(midia.getTipoMime()))
                .body(arquivo);
    }
}