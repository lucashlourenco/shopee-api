// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoDeEstoqueService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_principal.repository.endereco.EnderecoDeEstoqueRepository;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException; 
import jakarta.transaction.Transactional;

@Service
public class EnderecoDeEstoqueService {

    @Autowired
    private EnderecoDeEstoqueRepository repository;
    /*
    @Autowired 
    private VariacaoService variacaoService; // TODO: Fazer a VariaçãoService
    */

    /**
     * Retorna um endereço existente com base na semântica,
     * ou salva e retorna o novo endereço.
     * @param novoEndereco O EnderecoDeEstoque a ser persistido ou encontrado.
     * @return O EnderecoDeEstoque único (existente ou recém-criado).
     */
    @Transactional
    public EnderecoDeEstoque adicionarOuEncontarEnderecoDeEstoque(EnderecoDeEstoque novoEndereco) {
        
        // 1. Tenta encontrar um endereço igual.
        Optional<EnderecoDeEstoque> enderecoExistente = repository.findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplementoAndNome(
            novoEndereco.getCep(),
            novoEndereco.getRua(),
            novoEndereco.getBairro(),
            novoEndereco.getCidade(),
            novoEndereco.getEstado(),
            novoEndereco.getNumero(),
            novoEndereco.getComplemento(),
            novoEndereco.getNome()
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
     * @return O EnderecoDeEstoque encontrado.
     */
    public EnderecoDeEstoque obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Estoque não encontrado com ID: " + id + "."));
    }

    /**
     * Altera o endereço de estoque.
     * @param idEnderecoAtual O ID do endereço de estoque a ser alterado.
     * @param enderecoAlterado A Entidade EnderecoDeEstoque (com os novos dados).
     * @return O novo endereço de estoque a ser usado (o original alterado OU um novo/existente).
     */
    /**
    @Transactional
    public EnderecoDeEstoque alterarEndereco(UUID idEnderecoAtual, EnderecoDeEstoque enderecoAlterado) {
        
        // TODO: Tanto a InformacaoDeRetirada quanto a Variacao devem fazer o gerenciamneto da sua ligação com o EnderecoDeEstoque.
        EnderecoDeEstoque enderecoOriginal = obterPorID(idEnderecoAtual);

        // 1. Verifica se o novo endereço (com os dados alterados) já existe, se não, cria.
        EnderecoDeEstoque novoEnderecoUnico = adicionarOuEncontarEnderecoDeEstoque(enderecoAlterado);

        // Se o novo endereço encontrado tiver o mesmo ID do original, significa que a loja está em endereço já cadastrado.

        // 2. Se a semântica mudou (e gerou um novo/existente diferente):
        if (!enderecoOriginal.getId().equals(novoEnderecoUnico.getId())) {

            // InformacaoDeRetiradaService e VariacaoService se encarregarLayouto de:
            // 1) Desassociar-se do enderecoOriginal;
            // 2) Associar-se ao novoEnderecoUnico;
            // 3) Deletar o EnderecoOriginal se ele não estiver mais associado a nenhuma InformacaoDeRetirada ou Variacao.
            return novoEnderecoUnico;
        }
        
        // 3. Se o ID for o mesmo, a semântica do endereço não mudou. 
        else {
            
            int usoPorVariacoes = variacaoService.contarUsoPorEndereco(enderecoOriginal.getId());
            int usoPorInformacaoDeRetirada = enderecoOriginal.getInformacoesDeRetirada().size();


            // Verifica se este endereço é de uso exclusivo (apenas uma InformacaoDeRetirada usa)
            if (usoPorInformacaoDeRetirada < 2 && usoPorVariacoes < 2) { 
                
                // Aplica a alteração (incluindo o NOME, que pode ser o único campo alterado)
                enderecoOriginal.setHabilitado(Boolean.TRUE);
                enderecoOriginal.setCep(enderecoAlterado.getCep());
                enderecoOriginal.setRua(enderecoAlterado.getRua());
                enderecoOriginal.setBairro(enderecoAlterado.getBairro());
                enderecoOriginal.setCidade(enderecoAlterado.getCidade());
                enderecoOriginal.setEstado(enderecoAlterado.getEstado());
                enderecoOriginal.setNumero(enderecoAlterado.getNumero());
                enderecoOriginal.setComplemento(enderecoAlterado.getComplemento());
                enderecoOriginal.setReferencia(enderecoAlterado.getReferencia());
                enderecoOriginal.setNome(enderecoAlterado.getNome());
        
                return repository.save(enderecoOriginal);
            }
            
            // Se for compartilhado, não permite a alteração.
            else {
                throw new AdvertenciaException("Este endereço de estoque é compartilhado e não pode ser alterado.");
            }
        }
    } */

    /**
     * Apaga um endereço de cadastro (exclusão lógica).
     * @param id O ID do endereço.
     */
    @Transactional
    public EnderecoDeEstoque apagarEndereco(UUID id) {
        EnderecoDeEstoque endereco = obterPorID(id);
        endereco.setHabilitado(Boolean.FALSE);
        return repository.save(endereco);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoDeEstoque endereco = obterPorID(id);
        repository.delete(endereco);
    }
}