// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoComercialService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.repository.endereco.EnderecoComercialRepository;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException; 
import jakarta.transaction.Transactional;

@Service
public class EnderecoComercialService {

    @Autowired
    private EnderecoComercialRepository repository;

    /**
     * Retorna um endereço existente com base na semântica,
     * ou salva e retorna o novo endereço.
     * @param novoEndereco O EnderecoComercial a ser persistido ou encontrado.
     * @return O EnderecoComercial único (existente ou recém-criado).
     */
    @Transactional
    public EnderecoComercial adicionarOuEncontarEnderecoComercial(EnderecoComercial novoEndereco) {
        
        // 1. Tenta encontrar um endereço igual.
        Optional<EnderecoComercial> enderecoExistente = repository.findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplemento(
            novoEndereco.getCep(),
            novoEndereco.getRua(),
            novoEndereco.getBairro(),
            novoEndereco.getCidade(),
            novoEndereco.getEstado(),
            novoEndereco.getNumero(),
            novoEndereco.getComplemento()
        );

        // 2. Se encontrado, retorna a instância existente para reutilização.
        if (enderecoExistente.isPresent()) {
            return enderecoExistente.get();
        }

        // 3. Se não encontrado, salva o novo endereço.
        novoEndereco.setHabilitado(Boolean.TRUE);
        return repository.save(novoEndereco);
    }

    /**
     * Retorna um endereço existente com base no ID.
     * @param id O ID do endereço.
     * @return O EnderecoComercial encontrado.
     */
    public EnderecoComercial obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço Comercial não encontrado com ID: " + id));
    }

    /**
     * Altera o endereço de uma loja.
     * Esta rotina é complexa:
     * 1. Se o endereço for de uso exclusivo, ele é alterado.
     * 2. Se for compartilhado, o endereço antigo é preservado e a loja é 
     * associada a um novo/existente (Find or Create).
     * @param idEnderecoAtual O ID do endereço que a loja está usando atualmente.
     * @param enderecoAlterado A Entidade EnderecoComercial (com os novos dados).
     * @return O EnderecoComercial que a Loja deve passar a usar (o original alterado OU um novo/existente).
     */
    @Transactional
    public EnderecoComercial alterarEndereco(UUID idEnderecoAtual, EnderecoComercial enderecoAlterado) {
        
        // TODO: A loja deve fazer o gerenciamneto da ligação entre EnderecoComercial e Loja.
        // 1. Busca o endereço original.
        EnderecoComercial enderecoOriginal = obterPorID(idEnderecoAtual);

        // Veifica se o endereço alterado existe, se não, cria um novo.
        EnderecoComercial novoEnderecoUnico = adicionarOuEncontarEnderecoComercial(enderecoAlterado);

        // Se o novo endereço encontrado for o mesmo ID do original, significa que a loja está em endereço já cadastrado.

        // Se a semântica mudou (e gerou um novo/existente diferente):
        if (!enderecoOriginal.getId().equals(novoEnderecoUnico.getId())) {
             
            // A LojaService se encarregará de:
            // 1) Desassociar a loja do enderecoOriginal;
            // 2) Associar a loja ao novoEnderecoUnico;
            // 3) Deletar o EnderecoOriginal se a LojaService ao se desassociar da lista de lojas ficar vazia.

            return novoEnderecoUnico;
        }
        
        // Se o ID for o mesmo, a semântica do endereço não mudou, mas outros campos podem ter mudado.
        else {

            // Verifica se este endereço é de uso exclusivo.
            if (enderecoOriginal.getLojas().size() < 2) { 
                // Se for o único, altera o endereço original.
                enderecoOriginal.setHabilitado(Boolean.TRUE);
                enderecoOriginal.setCep(enderecoAlterado.getCep());
                enderecoOriginal.setRua(enderecoAlterado.getRua());
                enderecoOriginal.setBairro(enderecoAlterado.getBairro());
                enderecoOriginal.setCidade(enderecoAlterado.getCidade());
                enderecoOriginal.setEstado(enderecoAlterado.getEstado());
                enderecoOriginal.setNumero(enderecoAlterado.getNumero());
                enderecoOriginal.setComplemento(enderecoAlterado.getComplemento());
                enderecoOriginal.setReferencia(enderecoAlterado.getReferencia());
        
                return repository.save(enderecoOriginal);
            }
            
            // Se for compartilhado, não permite a alteração.
            else {
                throw new AdvertenciaException("Este endereço de cadastro é compartilhado e não pode ser alterado.");
            }
        }
    }

    /**
     * Apaga um endereço comercia (exclusão lógica).
     * @param id O ID do endereço.
     */
    @Transactional
    public EnderecoComercial apagarEndereco(UUID id) {
        EnderecoComercial endereco = obterPorID(id);
        endereco.setHabilitado(Boolean.FALSE);
        return repository.save(endereco);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoComercial endereco = obterPorID(id);
        repository.delete(endereco);
    }
}