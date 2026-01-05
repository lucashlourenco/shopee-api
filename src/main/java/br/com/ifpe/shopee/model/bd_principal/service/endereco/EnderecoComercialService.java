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

    // Método auxiliar para evitar repetição de setters
    private void copiarDados(EnderecoComercial origem, EnderecoComercial destino) {
        destino.setCep(origem.getCep());
        destino.setRua(origem.getRua());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setEstado(origem.getEstado());
        destino.setNumero(origem.getNumero());
        destino.setComplemento(origem.getComplemento());
        destino.setReferencia(origem.getReferencia());
    }

    /**
     * Retorna um endereço existente com base na semântica,
     * ou salva e retorna o novo endereço.
     * 
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
     * 
     * @param id O ID do endereço.
     * @return O EnderecoComercial encontrado.
     */
    public EnderecoComercial obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço Comercial com ID: " + id + " não encontrado."));
    }

    // Como a loja tem 1 enderço comercial, ao pegar Loja ela deve vir com o endereço.
    // Isso é tarefa do JPA.

    /**
     * Altera o endereço de uma loja.
     * Esta rotina é complexa:
     * 1. Se o endereço for de uso exclusivo, ele é alterado.
     * 2. Se for compartilhado, o endereço antigo é preservado e a loja é 
     * associada a um novo/existente.
     * 
     * @param idEnderecoAtual O ID do endereço que a loja está usando atualmente.
     * @param enderecoAlterado A Entidade EnderecoComercial (com os novos dados).
     * @return O EnderecoComercial que a Loja deve passar a usar (o original alterado OU um novo/existente).
     */
    @Transactional
    public EnderecoComercial alterarEndereco(UUID idEnderecoAtual, EnderecoComercial enderecoAlterado) {
        
        // TODO: A loja deve fazer o gerenciamneto da ligação entre EnderecoComercial e Loja.
        // 1. Busca o endereço original.
        EnderecoComercial enderecoOriginal = obterPorID(idEnderecoAtual);

        // 2. Verifica se o endereço alterado tem o mesmo ID do original, ou é ausente (tratar como se fosse o mesmo endereço)
        boolean eMesmoId = enderecoAlterado.getId() != null && enderecoOriginal.getId().equals(enderecoAlterado.getId());
        boolean eNovoNulo = enderecoAlterado.getId() == null;

        if (eMesmoId || eNovoNulo) {
            long lojasVinculadas = repository.contarLojasVinculadas(enderecoOriginal.getId());
            
            // 3. Verifica se o enderço atual (que é o mesmo que o alterado) é de uso exclusivo, se sim, ele deve ser alterado.
            if (lojasVinculadas < 2) {
                enderecoOriginal.setHabilitado(Boolean.TRUE);
                copiarDados(enderecoAlterado, enderecoOriginal);
                return repository.save(enderecoOriginal);
            }

            // 4. Se for compartilhado, não podemos editar o original. 
            // Tentamos encontrar um endereço que já tenha esses novos dados ou criamos um novo.
            else {
                enderecoAlterado.setId(null);
                // OBS1: Quem chamar essa rotina deve se encarregar de:
                // 1) Desassociar a loja do enderecoOriginal;
                // 2) Associar a loja ao novoEnderecoUnico;
                // 3) Só depois, pedir aqui ao EnderecoComercialService para limparSeOrfao (se o original ficou "órfao").
                return adicionarOuEncontarEnderecoComercial(enderecoAlterado);
            }
        }
        
        // 5. Se não é o mesmo endereco, salva e retorna o novo endereço
        else {
            // O mesmo que a OBS1. Deve acontecer sempre que entrar e a devolução tiver um id de endereco diferente do id do original.
            return adicionarOuEncontarEnderecoComercial(enderecoAlterado);
        }
    }

    /**
     * Apaga um endereço comercia (exclusão lógica).
     * 
     * @param id O ID do endereço.
     * @return O endereço apagado.
     */
    @Transactional
    public void apagarEndereco(UUID id) {
        EnderecoComercial endereco = obterPorID(id);

        // Verifica se o endereço possui outras lojas vinculadas
        long lojasVinculadas = repository.contarLojasVinculadas(endereco.getId());
        if (lojasVinculadas > 0) {
            throw new AdvertenciaException("Não é possível apagar este endereço pois existem lojas vinculadas a ele.");
        }

        endereco.setHabilitado(Boolean.FALSE);
        repository.save(endereco);
    }

    /**
     * Verifica se um endereço não possui mais alguma loja vinculada a ele e faz a exclusão lógica
     * 
     * @param idEndereco O ID do endereço.
     * @return Se o endereço foi excluído ou não.
     */
    @Transactional
    public boolean limparSeOrfao(UUID idEndereco) {
        // Verifica se o endereço existe (evitando de usar o obterPorID() por causa da execeção, aqui não precisa)
        Optional<EnderecoComercial> optEndereco = repository.findById(idEndereco);

        // Se o endereço existir
        if (optEndereco.isPresent()) {
            EnderecoComercial endereco = optEndereco.get();

            // Verifica se o endereço possui outras lojas vinculadas
            long lojasVinculadas = repository.contarLojasVinculadas(endereco.getId());
            if (lojasVinculadas == 0) {
                endereco.setHabilitado(Boolean.FALSE);
                repository.save(endereco);
                return true; // Sinaliza que foi excluído
            }
        }

        return false; // Não foi excluído (não existia, ou tinha gente usando)
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoComercial endereco = obterPorID(id);
        repository.delete(endereco);
    }
}