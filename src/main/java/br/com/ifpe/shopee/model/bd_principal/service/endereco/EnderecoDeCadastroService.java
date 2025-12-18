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

    /**
     * Retorna um endereço existente com base na semântica,
     * ou salva e retorna o novo endereço.
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
     * @param id O ID do endereço.
     * @return O EnderecoDeCadastro encontrado.
     */
    public EnderecoDeCadastro obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Cadastro não encontrado com ID: " + id));
    }

    /**
     * Altera o endereço de uma pessoa.
     * A rotina gerencia a complexidade de endereços compartilhados.
     * @param idEnderecoAtual O ID do endereço que a pessoa está usando atualmente.
     * @param enderecoAlterado A Entidade EnderecoDeCadastro (com os novos dados).
     * @return O EnderecoDeCadastro que a Pessoa deve passar a usar (o original alterado OU um novo/existente).
     */
    @Transactional
    public EnderecoDeCadastro alterarEndereco(UUID idEnderecoAtual, EnderecoDeCadastro enderecoAlterado) {
        
        EnderecoDeCadastro enderecoOriginal = obterPorID(idEnderecoAtual);

        // 1. Verifica se o novo endereço (com os dados alterados) já existe, se não, cria.
        EnderecoDeCadastro novoEnderecoUnico = adicionarOuEncontarEnderecoDeCadastro(enderecoAlterado);

        // Se o novo endereço encontrado tiver o mesmo ID do original, significa que a loja está em endereço já cadastrado.
        // 2. Se a semântica mudou (e gerou um novo/existente diferente):
        if (!enderecoOriginal.getId().equals(novoEnderecoUnico.getId())) {
            
            // Quem chamar essa rotina deve se encarregarLayouto de:
            // 1) Desassociar a pessoa do enderecoOriginal;
            // 2) Associar a pessoa ao novoEnderecoUnico;
            // 3) Só depois, pedir aqui ao EnderecoDeCadastroService para limparSeOrfao se o original ficou "órfao".
            return novoEnderecoUnico;
        }
        
        // 3. Se o ID for o mesmo, a semântica do endereço não mudou. 
        else {
            
            // Verifica se este endereço é de uso exclusivo
            if (enderecoOriginal.getPessoas().size() < 2) {
                // Se for o único, altera o endereço original (afetando apenas o registro dela).
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
     * Apaga um endereço de cadastro (exclusão lógica).
     * @param id O ID do endereço.
     */
    @Transactional
    public EnderecoDeCadastro apagarEndereco(UUID id) {
        EnderecoDeCadastro endereco = obterPorID(id);
        endereco.setHabilitado(Boolean.FALSE);
        return repository.save(endereco);
    }

    /**
     * Verifica se um endereço não possui mais ninguém vinculado a ele.
     * @param idEndereco O ID do endereço.
     */
    @Transactional
    public boolean limparSeOrfao(UUID idEndereco) {
        EnderecoDeCadastro endereco = obterPorID(idEndereco);
        if (endereco.getPessoas() == null || endereco.getPessoas().isEmpty()) {
            apagarEndereco(idEndereco);
            return true;
        }
        return false;
    }
}