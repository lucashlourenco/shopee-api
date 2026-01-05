// src/main/java/br/com/ifpe/shopee/model/bd_principal/service/contato/ContatoDeUsuarioService.java

package br.com.ifpe.shopee.model.bd_principal.service.contato;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.contato.ContatoDeUsuarioRepository;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class ContatoDeUsuarioService {

    @Autowired
    private ContatoDeUsuarioRepository repository;
    
    @Autowired
    private TipoDeUsuarioRepository tipoDeUsuarioRepository; // Usado para buscar o usuário pelo ID

    @Autowired
    private ValidadorDeAcesso validador;

    /**
     * Serviço para adicionar um novo contato a um usuário existente.
     * Implementa a regra de unicidade de domínio: (Usuario + Valor + Tipo) deve ser único.
     * 
     * @param idUsuario O ID do usuário onde o contato sera adicionado.
     * @param novoContato A Entidade ContatoDeUsuario (convertida a partir do Request).
     * @param usuarioLogado O Usuário que está logado.
     * @return O ContatoDeUsuario salvo.
     */
    @Transactional
    public ContatoDeUsuario adicionarContatoDeUsuario(UUID idUsuario, ContatoDeUsuario novoContato, Usuario usuarioLogado) {
        // Valida se está adicionando contatos na própria conta
        validador.validarPosse(idUsuario, usuarioLogado);

        // A busca será genérica por qualquer tipo de usuário do sistema
        TipoDeUsuario usuario = tipoDeUsuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID: " + idUsuario + " não encontrado."));

        // Verificar unicidade (Regra: Usuario + Valor + Tipo deve ser único)
        ContatoDeUsuario contatoExistente = repository.findByUsuarioIdAndValorAndTipo(
            idUsuario, 
            novoContato.getValor(), 
            novoContato.getTipo()
        );

        if (contatoExistente != null) {
            throw new EntidadeDuplicadaException("O contato de tipo '"
                                                    + novoContato.getTipo()
                                                    + "' com valor '"
                                                    + novoContato.getValor() +
                                                    "' já existe para este usuário.");
        }

        // Finalizar a entidade e salvar
        novoContato.setHabilitado(Boolean.TRUE);
        novoContato.setUsuario(usuario);
        
        return repository.save(novoContato);
    }

    /**
     * Serviço para obter um contato de usuário pelo ID.
     * 
     * @param id O ID do contato.
     * @return O ContatoDeUsuario encontrado.
     */
    public ContatoDeUsuario obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Contato de usuário com ID: " + id + " não encontrado."));
    }

    /**
     * Listar todos os contatos de um Usuário.
     * 
     * @param idUsuario O ID do Usuário onde os contatos serão buscados.
     * @return A lista de contatos.
     */
    public List<ContatoDeUsuario> listarContatosDeUsuario(UUID idUsuario) {
        return repository.findByUsuarioId(idUsuario);
    }

    /**
     * Serviço para alterar um contato de usuário existente.
     * 
     * @param id O ID do contato.
     * @param contatoAlterado A Entidade ContatoDeUsuario (convertida a partir do Request).
     * @param usuarioLogado O Usuário que está logado.
     * @return O ContatoDeUsuario alterado.
     */
    @Transactional
    public ContatoDeUsuario alterarContatoDeUsuario(UUID id, ContatoDeUsuario contatoAlterado, Usuario usuarioLogado) {
        ContatoDeUsuario contatoOriginal = obterPorID(id);

        // Valida se o contato pertence ao usuário logado
        validador.validarPosse(contatoOriginal.getUsuario().getId(), usuarioLogado);

        // Verificando se o contato foi realmente alterado
        // Preparando o contatoAlterado para a comparação de igualdade
        contatoAlterado.setUsuario(contatoOriginal.getUsuario());
        contatoAlterado.setId(null); 
        
        if (contatoOriginal.equals(contatoAlterado) && 
            contatoOriginal.isEVisivel() == contatoAlterado.isEVisivel()) {
            throw new AdvertenciaException("Nenhuma alteração foi realizada no contato.");
        }

        // Verificar duplicidade apenas se Valor ou Tipo mudaram
        if (!contatoOriginal.getValor().equals(contatoAlterado.getValor()) || 
            !contatoOriginal.getTipo().equals(contatoAlterado.getTipo())) {
            
            ContatoDeUsuario contatoDuplicado = repository.findByUsuarioIdAndValorAndTipo(
                contatoOriginal.getUsuario().getId(), 
                contatoAlterado.getValor(), 
                contatoAlterado.getTipo()
            );

            // Se for encontrado um contato duplicado, e esse contato não for o original que estamos alterando...
            if (contatoDuplicado != null && !contatoDuplicado.getId().equals(contatoOriginal.getId())) {
                throw new EntidadeDuplicadaException("O novo valor/tipo de contato já existe para este usuário.");
            }
        }

        // Aplicação das alterações
        contatoOriginal.setValor(contatoAlterado.getValor());
        contatoOriginal.setTipo(contatoAlterado.getTipo());
        contatoOriginal.setNome(contatoAlterado.getNome());
        contatoOriginal.setEVisivel(contatoAlterado.isEVisivel());
        
        return repository.save(contatoOriginal);
    }

    /**
     * Apaga um contato de usuário (exclusão lógica).
     * 
     * @param usuarioLogado O usuário logado.
     * @param id O ID do contato.
     * @return O ContatoDeUsuario apagado.
     */
    @Transactional
    public ContatoDeUsuario apagarContatoDeUsuario(UUID id, Usuario usuarioLogado) {
        ContatoDeUsuario contato = obterPorID(id);

        // Valida se o contato pertence ao usuário logado
        validador.validarPosse(contato.getUsuario().getId(), usuarioLogado);
        contato.setHabilitado(Boolean.FALSE); 

        return repository.save(contato);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um contato de usuário permanentemente.
     * 
     * @param id O ID do contato.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        ContatoDeUsuario contato = obterPorID(id);
        repository.delete(contato);
    }
}