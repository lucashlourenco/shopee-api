// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoDeCadastroService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.repository.endereco.EnderecoDeCadastroRepository;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException; 
import jakarta.transaction.Transactional;

@Service
public class EnderecoDeCadastroService {

    @Autowired
    private EnderecoDeCadastroRepository repository;

    // Método auxiliar para evitar repetição de setters
    private void copiarDados(EnderecoDeCadastro origem, EnderecoDeCadastro destino) {
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
     * @param novoEndereco O EnderecoDeCadastro a ser persistido ou encontrado.
     * @return O EnderecoDeCadastro único (existente ou recém-criado).
     */
    @Transactional
    public EnderecoDeCadastro adicionarOuEncontarEnderecoDeCadastro(EnderecoDeCadastro novoEndereco) {
        
        // 1. Tenta encontrar um endereço igual.
        Optional<EnderecoDeCadastro> enderecoExistente = repository.findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplemento(
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
     * @return O EnderecoDeCadastro encontrado.
     */
    public EnderecoDeCadastro obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Cadastro com ID: " + id + " não encontrado."));
    }

    // Como a pessoa tem 1 enderço de cadastro, ao pegar Pessoa ela deve vir com o endereço.
    // Isso é tarefa do JPA.

    /**
     * Altera o endereço de uma pessoa.
     * A rotina gerencia a complexidade de endereços compartilhados.
     * 
     * @param idEnderecoAtual O ID do endereço que a pessoa está usando atualmente.
     * @param enderecoAlterado A Entidade EnderecoDeCadastro (com os novos dados).
     * @return O EnderecoDeCadastro que a Pessoa deve passar a usar (o original alterado OU um novo/existente).
     */
    @Transactional
    public EnderecoDeCadastro alterarEndereco(UUID idEnderecoAtual, EnderecoDeCadastro enderecoAlterado) {
        
        // 1. Resgata o endereço original do bd 
        EnderecoDeCadastro enderecoOriginal = obterPorID(idEnderecoAtual);

        // 2. Verifica se o endereço alterado tem o mesmo ID do original, ou é ausente (tratar como se fosse o mesmo endereço)
        boolean eMesmoId = enderecoAlterado.getId() != null && enderecoOriginal.getId().equals(enderecoAlterado.getId());
        boolean eNovoNulo = enderecoAlterado.getId() == null;

        if (eMesmoId || eNovoNulo) {
            long pessoasVinculadas = repository.contarPessoasVinculadas(enderecoOriginal.getId());
            
            // 3. Verifica se o enderço atual (que é o mesmo que o alterado) é de uso exclusivo, se sim, ele deve ser alterado.
            if (pessoasVinculadas < 2) {
                enderecoOriginal.setHabilitado(Boolean.TRUE);
                copiarDados(enderecoAlterado, enderecoOriginal);
                return repository.save(enderecoOriginal);
            }

            // 4. Se for compartilhado, não podemos editar o original. 
            // Tentamos encontrar um endereço que já tenha esses novos dados ou criamos um novo.
            else {
                enderecoAlterado.setId(null);
                // OBS1: Quem chamar essa rotina deve se encarregar de:
                // 1) Desassociar a pessoa do enderecoOriginal;
                // 2) Associar a pessoa ao novoEnderecoUnico;
                // 3) Só depois, pedir aqui ao EnderecoDeCadastroService para limparSeOrfao (se o original ficou "órfao").
                return adicionarOuEncontarEnderecoDeCadastro(enderecoAlterado);
            }
        }

        // 5. Se não é o mesmo endereco, salva e retorna o novo endereço
        else {
            // O mesmo que a OBS1. Deve acontecer sempre que entrar e a devolução tiver um id de endereco diferente do id do original.
            return adicionarOuEncontarEnderecoDeCadastro(enderecoAlterado);
        }
    }

    /**
     * Apaga um endereço de cadastro (exclusão lógica).
     * 
     * @param id O ID do endereço.
     * @return Se o endereço foi excluído ou não.
     */
    @Transactional
    public void apagarEndereco(UUID id) {
        EnderecoDeCadastro endereco = obterPorID(id);

        // Verifica se o endereço possui outras pessoas vinculadas
        long pessoasVinculadas = repository.contarPessoasVinculadas(endereco.getId());
        if (pessoasVinculadas > 0) {
            throw new AdvertenciaException("Não é possível apagar este endereço pois existem pessoas vinculadas a ele.");
        }

        endereco.setHabilitado(Boolean.FALSE);
        repository.save(endereco);
    }

    /**
     * Verifica se um endereço não possui mais ninguém vinculado a ele e faz a exclusão lógica
     * 
     * @param idEndereco O ID do endereço.
     * @return Se o endereço foi excluído ou não.
     */
    @Transactional
    public boolean limparSeOrfao(UUID idEndereco) {
        // Verifica se o endereço existe (evitando de usar o obterPorID() por causa da execeção, aqui não precisa)
        Optional<EnderecoDeCadastro> optEndereco = repository.findById(idEndereco);

        // Se o endereço existir
        if (optEndereco.isPresent()) {
            EnderecoDeCadastro endereco = optEndereco.get();

            // Verifica se o endereço possui outras pessoas vinculadas
            long pessoasVinculadas = repository.contarPessoasVinculadas(endereco.getId());
            if (pessoasVinculadas == 0) {
                endereco.setHabilitado(Boolean.FALSE);
                repository.save(endereco);
                return true; // Sinaliza que foi excluído
            }
        }

        return false; // Não foi excluído (não existia, ou tinha gente usando)
    }

    /**
     * ATENÇÃO: Use com cautela. Apaga um endereço de cadastro permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoDeCadastro endereco = obterPorID(id);
        repository.delete(endereco);
    }
}