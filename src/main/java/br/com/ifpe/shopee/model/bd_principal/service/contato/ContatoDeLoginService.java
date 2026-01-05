// src/main/java/br/com/ifpe/shopee/model/bd_principal/service/contato/ContatoDeLoginService.java

package br.com.ifpe.shopee.model.bd_principal.service.contato;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.repository.UsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.contato.ContatoDeLoginRepository;
import br.com.ifpe.shopee.util.exception.AcessoNegadoException;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException; 
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class ContatoDeLoginService {

    @Autowired
    private ContatoDeLoginRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ValidadorDeAcesso validador;

    @Transactional
    private ContatoDeLogin salvarContatoDeLogin(ContatoDeLogin contato) {
        // Verifica se o contato possui um usuário
        if (contato.getUsuario() == null) {
            throw new IllegalArgumentException("Não é possível salvar uma credencial sem usuário vinculado.");
        }    
        if (repository.findByValor(contato.getValor()).isPresent()) {
            throw new EntidadeDuplicadaException("O valor de login '" 
                                                 + contato.getValor() 
                                                 + "' já está em uso por outro usuário.");
        }
        contato.setHabilitado(Boolean.TRUE);
        return repository.save(contato);
    }

    /**
     * Serviço para salvar um novo Contato de Login. Implementa a regra de unicidade
     * global, limite de 2 credenciais ativas e gerencia a criação de um novo Usuário.
     * 
     * @param idUsuario O ID do usuário a qual o contato de login pertence.
     * @param novaCredencial O ContatoDeLogin a ser salvo.
     * @param usuarioLogado O Usuário que está logado.
     * @return O ContatoDeLogin salvo.
     */
    @Transactional
    public ContatoDeLogin adicionarCredencial(UUID idUsuario, ContatoDeLogin novaCredencial, Usuario usuarioLogado) {
        // Na hora de criar um usuário ele não tem ID. Então...
        if (idUsuario == null) {
            return salvarContatoDeLogin(novaCredencial);
        }

        // Se for adição posterior (via endpoint), validamos a posse
        validador.validarPosse(idUsuario, usuarioLogado);

        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID: " + idUsuario + " não encontrado."));

        // Validação de unicidade global
        if (repository.findByValor(novaCredencial.getValor()).isPresent()) {
            throw new EntidadeDuplicadaException("O valor de login '" 
                                                 + novaCredencial.getValor() 
                                                 + "' já está em uso por outro usuário.");
        }

        // Limite de 2 credenciais
        long qtdAtual = repository.countByUsuarioIdAndHabilitadoTrue(idUsuario);
        if (qtdAtual >= 2) {
            throw new AdvertenciaException("O usuário já possui o número máximo de credenciais ativas (2).");
        }

        novaCredencial.setUsuario(usuario);
        novaCredencial.setHabilitado(Boolean.TRUE);
        
        return repository.save(novaCredencial);
    }
    
    /**
     * Serviço para obter um contato de usuário pelo ID.
     * 
     * @param id O ID do contato.
     * @return O ContatoDeUsuario encontrado.
     */
    public ContatoDeLogin obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Login com ID: " + id + " não encontrado."));
    }

    /**
     * Serviço para buscar um ContatoDeLogin pelo valor. 
     * Usado principalmente por serviços de Autenticação.
     *
     * @param valor O valor do login (email/telefone).
     * @return O ContatoDeLogin encontrado.
     */
    public ContatoDeLogin obterPorValor(String valor) {
        return repository.findByValor(valor)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Login não encontrado para o valor: " + valor));
    }

    /**
     * Apaga um contato de login (crediencial) com exclusão lógica.
     * Implementa a regra a regra de 1 credencial mínima.
     * Para apagara a conta deve-se apagar Cliente/Vendedor.
     * 
     * @param idUsuario O ID do usuário a qual o contato de login pertence.
     * @param idCredencial O ID da credencial a ser apagado.
     * @param usuarioLogado O Usuário que está logado.
     * @return O ContatoDeLogin apagado.
     */
    @Transactional
    public ContatoDeLogin apagarCredencial(UUID idUsuario, UUID idCredencial, Usuario usuarioLogado) {
        ContatoDeLogin contato = obterPorID(idCredencial);

        // 1. Validação de Posse 
        validador.validarPosse(idUsuario, usuarioLogado);

        // 2. Validação de Integridade (se a credencial pertence ao usuário)
        if (!contato.getUsuario().getId().equals(idUsuario)) {
            throw new AcessoNegadoException("Esta credencial não pertence ao usuário informado.");
        }

        // 3. Validação de Mínimo
        long qtdAtual = repository.countByUsuarioIdAndHabilitadoTrue(idUsuario);
        if (qtdAtual <= 1) {
            throw new AdvertenciaException("Não é possível remover a única credencial de acesso. " +
                                            "Adicione outra antes de remover esta. " +
                                            "Para apagar a conta, apague Cliente/Vendedor.");
        }

        contato.setHabilitado(Boolean.FALSE);
        return repository.save(contato);
    }

    /**
     * Apaga (exclusão lógica) todas as credenciais (ContatoDeLogin)
     * de uma Pessoa.
     * 
     * @param idPessoa O ID da Pessoa a qual as credenciais pertencem.
     * @return O usuario com as credenciais desabilitadas.
     */
    @Transactional
    public Usuario apagarTodasAsCredenciaisDeUsuario(Usuario usuario) {
        if (usuario.getCredenciais() != null) {
            for (ContatoDeLogin credencial : usuario.getCredenciais()) {
                credencial.setHabilitado(Boolean.FALSE);
                repository.save(credencial);
            }
        }

        else {
            throw new RecursoNaoEncontradoException("Usuário com ID: " + usuario.getId() + " não possui credenciais.");
        }

        return usuario;
    }

    /**
     * ATENÇÃO: Use com cautela. Apaga um contato de login permanentemente.
     * 
     * @param id O ID do contato.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        ContatoDeLogin contato = obterPorID(id);
        repository.delete(contato);
    }
}