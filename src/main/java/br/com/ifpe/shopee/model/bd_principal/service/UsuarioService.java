// src/main/java/br.com/ifpe/shopee.model/bd_principal/service/UsuarioService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.repository.UsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeLoginService;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;
    
    @Autowired 
    private ContatoDeLoginService contatoDeLoginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidadorDeAcesso validador;

    /**
     * Cria e persiste um novo Usuario para uma Pessoa existente.
     * 
     * @param pessoa A Pessoa à qual o Usuario será vinculado.
     * @param senha A senha.
     * @param login O valor (email/telefone) do ContatoDeLogin inicial.
     * @return O Usuario salvo.
     */
    @Transactional
    public Usuario adicionarUsuario(Pessoa pessoa, String senha, ContatoDeLogin contatoLoginInicial) {

        if (repository.findByPessoaId(pessoa.getId()).isPresent()) {
            throw new EntidadeDuplicadaException("Esta pessoa já possui um cadastro de usuário.");
        }
        
        String senhaCriptografada = passwordEncoder.encode(senha); 
        
        // 1. Cria a entidade Usuario
        Usuario usuario = Usuario.builder()
                                 .pessoa(pessoa)
                                 .senha(senhaCriptografada)
                                 .build();
        
        // 2. Salva o Usuario
        Usuario usuarioSalvo = repository.save(usuario);
        
        // 3. Configura e Salva o ContatoDeLogin inicial
        // (O objeto contatoLoginInicial veio montado do request, mas sem o usuário pai)
        contatoLoginInicial.setUsuario(usuarioSalvo);
        contatoLoginInicial.setHabilitado(Boolean.TRUE);
        contatoDeLoginService.adicionarCredencial(null, contatoLoginInicial, usuarioSalvo);
        
        return usuarioSalvo;
    }

    /**
     * Retorna um usuário existente com base no ID.
     * 
     * @param id O ID do usuário.
     * @return O Usuario encontrado.
     */
    public Usuario obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID: '" + id + "' não encontrado."));
    }
    
    /**
     * Retorna um usuário existente com base no login.
     * 
     * @param login O login (ContatoDeLogin.getValor()).
     * @return O Usuario encontrado.
     */
    public Usuario buscarUsuarioPorCredencial(String login) {
        // Encontra o usuário pelo login (email/telefone)
        Usuario usuario = repository.findByLoginValor(login)
                                    .orElseThrow(() -> new RecursoNaoEncontradoException("Credenciais inválidas."));

        return usuario;
    }

    /**
     * Altera a senha de um usuário.
     * 
     * @param idUsuario O ID do usuário.
     * @param novaSenha A nova senha.
     */
    @Transactional
    public void alterarSenha(UUID idUsuario, String novaSenha, Usuario usuarioLogado) {
        // Valida se o ID que eu quero alterar é o MEU ID
        validador.validarPosse(idUsuario, usuarioLogado);

        Usuario usuario = obterPorID(idUsuario);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        
        repository.save(usuario);
    }

    /**
     * Apaga um usuário (exclusão lógica) com todas as suas credenciais.
     * 
     * @param id O ID do usuário.
     * @return O Usuario apagado.
     */
    @Transactional
    public Usuario apagarUsuario(UUID idUsuario, Usuario usuarioLogado) {
        // Valida se estou tentando me apagar
        validador.validarPosse(idUsuario, usuarioLogado);

        Usuario usuario = obterPorID(idUsuario);
        usuario.setHabilitado(Boolean.FALSE);

        // Apaga (exclusão lógica) as credenciais do usuário
        contatoDeLoginService.apagarTodasAsCredenciaisDeUsuario(usuario);

        return repository.save(usuario);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        Usuario usuario = obterPorID(id);
        repository.delete(usuario);
    }
}