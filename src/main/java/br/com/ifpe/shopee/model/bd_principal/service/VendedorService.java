// src/main/java/br/com/ifpe.shopee.model/bd_principal/service/VendedorService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Vendedor;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroVendedorRequest;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;

@Service
public class VendedorService {

    @Autowired
    private PessoaService pessoaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LojaService lojaService;
    
    @Autowired
    private TipoDeUsuarioRepository tipoDeUsuarioRepository;

    /**
     * Busca um Vendedor pelo ID.
     * @param id O ID do Vendedor.
     * @return O Vendedor encontrado.
     */
    public Vendedor obterPorID(UUID id) {
        return (Vendedor) tipoDeUsuarioRepository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Vendedor com ID: '" + id + "' não encontrado."));
    }
    
    /**
     * Orquestra a criação completa de um novo Vendedor (Pessoa, Usuario, Vendedor e Loja).
     * @param request O DTO de Orquestração.
     * @return O Vendedor recém-criado.
     */
    @Transactional
    public Vendedor adicionarVendedor(CadastroVendedorRequest request) {
        
        Pessoa pessoa;
        
        // 1. Gerenciar a Pessoa e Validar o Papel
        try {
            pessoa = pessoaService.obterPorCPF(request.getPessoa().getCpf());
            
            if (tipoDeUsuarioRepository.existsVendedorByPessoaId(pessoa.getId())) {
                throw new AdvertenciaException("Esta Pessoa já possui um perfil de Vendedor ativo.");
            }
        }
        catch (RecursoNaoEncontradoException e) {
            Pessoa novaPessoa = request.getPessoa().build();
            pessoa = pessoaService.adicionarPessoa(novaPessoa, request.getPessoa().getEnderecoDeCadastro().build());
        }

        // 2. Criar a Conta (Usuário)
        Usuario usuario = usuarioService.adicionarUsuario(
            pessoa, 
            request.getConta().getSenha(), 
            request.getConta().getLogin()
        );
        
        // 3. Criar e Salvar a Loja (Se o DTO a contiver)
        Loja lojaSalva = null;
        if (request.getLoja() != null) {
            Loja novaLoja = request.getLoja().build();
            lojaSalva = lojaService.adicionarLoja(
                novaLoja, 
                request.getLoja().getEnderecoComercial().build()
            );
        }
    
        // 4. Criar o Papel (Vendedor)
        Vendedor vendedor = new Vendedor();
        vendedor.setHabilitado(Boolean.TRUE);
        vendedor.setDataDeCadastro(LocalDateTime.now());
        vendedor.setUsuario(usuario);
        vendedor.setCnpj(request.getCnpj()); // Pode ser nulo
        vendedor.setLoja(lojaSalva);         // Pode ser nulo
        
        Vendedor vendedorSalvo = (Vendedor) tipoDeUsuarioRepository.save(vendedor);
        
        return vendedorSalvo;
    }
    
    /**
     * Apaga um Vendedor (exclusão lógica) e orquestra a exclusão do Usuario e Pessoa
     * se não houverem mais papéis/usuários ativos.
     * @param id O ID do Vendedor.
     * @return O Vendedor desabilitado.
     */
    @Transactional
    public Vendedor apagarVendedor(UUID id) {
        Vendedor vendedor = obterPorID(id);
        Usuario usuario = vendedor.getUsuario();
        Pessoa pessoa = usuario.getPessoa();
        Loja loja = vendedor.getLoja();
        
        // 1. Exclusão Lógica do Papel (Vendedor)
        vendedor.setHabilitado(Boolean.FALSE);
        // vendedor.setLoja(null); // Optando por não desvincula a Loja do Vendedor
        Vendedor vendedorDesabilitado = (Vendedor) tipoDeUsuarioRepository.save(vendedor);

        // 2. Apagar o Usuário (se for o último papel ativo)
        long papeisAtivos = tipoDeUsuarioRepository.countTiposAtivosByUsuarioId(usuario.getId());

        if (papeisAtivos == 0) {
            usuarioService.apagarUsuario(usuario.getId()); 
            
            // 3. Apagar a Pessoa (se for o último usuário ativo)
            long usuariosAtivos = pessoaService.countUsuariosAtivosByPessoaId(pessoa.getId()); 

            if (usuariosAtivos == 0) {
                pessoaService.apagarPessoa(pessoa.getId());
            }
        }
        
        // 4. Apagar a Loja.
        if (loja != null) {
            lojaService.apagarLoja(loja.getId());
        }
        
        return vendedorDesabilitado;
    }
}