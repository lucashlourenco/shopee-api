// src/main/java/br.com/ifpe/shopee.model/bd_principal/service/UsuarioService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.repository.UsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.service.contato.ContatoDeLoginService;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;
    
    @Autowired 
    private ContatoDeLoginService contatoDeLoginService; 

    /**
     * Cria e persiste um novo Usuario para uma Pessoa existente.
     * @param pessoa A Pessoa à qual o Usuario será vinculado.
     * @param senha A senha.
     * @param login O valor (email/telefone) do ContatoDeLogin inicial.
     * @return O Usuario salvo.
     */
    @Transactional
    public Usuario adicionarUsuario(Pessoa pessoa, String senha, String login) {
        
        // TODO: Criptografia da senha
        String senhaArmazenada = senha; 
        
        // 1. Cria a entidade Usuario
        Usuario usuario = Usuario.builder()
                                 .pessoa(pessoa)
                                 .senha(senhaArmazenada)
                                 .build();
        
        // 2. Salva o Usuario (para obter o ID)
        Usuario usuarioSalvo = repository.save(usuario);
        
        // 3. Cria e salva o ContatoDeLogin inicial
        ContatoDeLogin contatoLogin = new ContatoDeLogin();

        contatoLogin.setValor(login);
        contatoLogin.setUsuario(usuarioSalvo);
        contatoLogin.setHabilitado(Boolean.TRUE);
        
        contatoDeLoginService.salvar(contatoLogin);
        
        // 4. Retorna o usuário completo
        return usuarioSalvo; 
    }

    /**
     * Retorna um usuário existente com base no ID.
     * @param id O ID do usuário.
     * @return O Usuario encontrado.
     */
    public Usuario obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID: '" + id + "' não encontrado."));
    }
    
    /**
     * Altera a senha de um usuário.
     * @param idUsuario O ID do usuário.
     * @param novaSenha A nova senha.
     */
    @Transactional
    public void alterarSenha(UUID idUsuario, String novaSenha) {
        Usuario usuario = obterPorID(idUsuario);
        
        // Substituindo: usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setSenha(novaSenha); 
        repository.save(usuario);
    }

    /**
     * Apaga um usuário (exclusão lógica).
     * @param id O ID do usuário.
     * @return O Usuario apagado.
     */
    @Transactional
    public Usuario apagarUsuario(UUID id) {
        Usuario usuario = obterPorID(id);
        usuario.setHabilitado(Boolean.FALSE);
        return repository.save(usuario);
    }
    
    /**
     * Autentica um usuário. Depois poderá ser utilizado o spring security.
     * @param login 
     * @param senha
     * @return
     */
    public Usuario autenticar(String login, String senha) {
        // Encontra o usuário pelo login (email/telefone)
        Usuario usuario = repository.findByLoginValor(login)
                                    .orElseThrow(() -> new RecursoNaoEncontradoException("Credenciais inválidas."));
        
        // Compara a senha (sem criptografia no momento)
        if (!usuario.getSenha().equals(senha)) {
             throw new AdvertenciaException("Credenciais inválidas.");
        }

        return usuario;
    }
}