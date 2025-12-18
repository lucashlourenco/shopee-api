// src/main/java/br/com/ifpe/shopee/model/bd_principal/service/contato/ContatoDeUsuarioService.java

package br.com.ifpe.shopee.model.bd_principal.service.contato;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.repository.contato.ContatoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.service.ClienteService;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;

@Service
public class ContatoDeUsuarioService {

    @Autowired
    private ContatoDeUsuarioRepository repository;
    
    @Autowired
    private ClienteService clienteService; // Usado para buscar o usuário principal

    /**
     * Serviço para obter um contato de usuário pelo ID.
     * @param id O ID do contato.
     * @return O ContatoDeUsuario encontrado.
     */
    public ContatoDeUsuario obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Contato de usuário não encontrado com ID: " + id));
    }

    /**
     * Serviço para adicionar um novo contato a um usuário existente.
     * Implementa a regra de unicidade de domínio: (Usuario + Valor + Tipo) deve ser único.
     *
     * @param idUsuario O ID do usuário onde o contato sera adicionado.
     * @param novoContato A Entidade ContatoDeUsuario (convertida a partir do Request).
     * @return O ContatoDeUsuario salvo.
     */
    @Transactional
    public ContatoDeUsuario adicionarContatoUsuario(UUID idUsuario, ContatoDeUsuario novoContato) {
        // Verificar e obter a Loja
        TipoDeUsuario usuario = clienteService.obterPorID(idUsuario);

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
     * Serviço para alterar um contato de usuário existente.
     * 
     * @param id                O ID do contato.
     * @param contatoAlterado   A Entidade ContatoDeUsuario (convertida a partir do Request).
     * @return                  O ContatoDeUsuario alterado.
     */
    @Transactional
    public ContatoDeUsuario alterarContato(UUID id, ContatoDeUsuario contatoAlterado) {
        
        ContatoDeUsuario contatoOriginal = obterPorID(id);

        // Verificar se o novo Valor/Tipo já existe para o mesmo usuário,
        // mas apenas se o valor ou tipo foram alterados.

        if (!contatoOriginal.getValor().equals(contatoAlterado.getValor()) 
            || !contatoOriginal.getTipo().equals(contatoAlterado.getTipo()))
        {
            ContatoDeUsuario contatoDuplicado = repository.findByUsuarioIdAndValorAndTipo(
                contatoOriginal.getUsuario().getId(), 
                contatoAlterado.getValor(), 
                contatoAlterado.getTipo()
            );

            // Se for encontrado um contato duplicado, e esse contato não for o original que
            // estamos alterando...
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
     * Listar todos os contatos de um Usuário.
     * 
     * @param idUsuario O ID do Usuário onde os contatos serão buscados.
     * @return A lista de contatos.
     */
    public List<ContatoDeUsuario> listarContatos(UUID idUsuario) {
        return repository.findByUsuarioId(idUsuario);
    }

    /**
     * Apaga um contato de usuário (exclusão lógica).
     * @param id O ID do contato.
     * @return O ContatoDeUsuario apagado.
     */
    @Transactional
    public ContatoDeUsuario apagarContato(UUID id) {
        ContatoDeUsuario contato = obterPorID(id);
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