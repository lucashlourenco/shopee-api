// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoDeEstoqueService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Método auxiliar para evitar repetição de setters
    private void copiarDados(EnderecoDeEstoque origem, EnderecoDeEstoque destino) {
        destino.setCep(origem.getCep());
        destino.setRua(origem.getRua());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setEstado(origem.getEstado());
        destino.setNumero(origem.getNumero());
        destino.setComplemento(origem.getComplemento());
        destino.setReferencia(origem.getReferencia());
        destino.setNome(origem.getNome());
    }

    /**
     * Retorna um endereço existente com base na semântica,
     * ou salva e retorna o novo endereço.
     * 
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
     * 
     * @param id O ID do endereço.
     * @return O EnderecoDeEstoque encontrado.
     */
    public EnderecoDeEstoque obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Estoque não encontrado com ID: " + id + "."));
    }

    /**
     * Lista endereços vinculados a uma loja específica.
     * 
     * @param idLoja O ID da loja.
     * @return Uma lista de endereços de estoque.
     */
    public List<EnderecoDeEstoque> listarPorLojaId(UUID idLoja) {
        return repository.findByLojasId(idLoja);
    }

    /**
     * Altera o endereço de estoque.
     * Gerencia a complexidade de endereços compartilhados por múltiplas lojas.
     * 
     * @param idEnderecoAtual O ID do endereço que a loja está usando atualmente.
     * @param enderecoAlterado A Entidade EnderecoDeEstoque (com os novos dados).
     * @return O novo endereço de estoque a ser usado (o original alterado OU um novo/existente).
     */
    @Transactional
    public EnderecoDeEstoque alterarEndereco(UUID idEnderecoAtual, EnderecoDeEstoque enderecoAlterado) {
        // TODO: Tanto a InformacaoDeRetirada quanto a Variacao devem fazer o gerenciamneto da sua ligação com o EnderecoDeEstoque.
        
        // 1. Busca o endereço original.
        EnderecoDeEstoque enderecoOriginal = obterPorID(idEnderecoAtual);

        // 2. Verifica se o endereço alterado tem o mesmo ID do original ou é nulo/novo.
        boolean eMesmoId = enderecoAlterado.getId() != null && enderecoOriginal.getId().equals(enderecoAlterado.getId());
        boolean eNovoNulo = enderecoAlterado.getId() == null;

        if (eMesmoId || eNovoNulo) {
            long lojasVinculadas = repository.contarLojasVinculadas(enderecoOriginal.getId());
            
            // 3. Se for de uso exclusivo (vinculado a apenas 1 loja), altera o próprio registro.
            if (lojasVinculadas < 2) {
                enderecoOriginal.setHabilitado(Boolean.TRUE);
                copiarDados(enderecoAlterado, enderecoOriginal);
                return repository.save(enderecoOriginal);
            }

            // 4. Se for compartilhado, não podemos editar o original para não afetar outras lojas.
            // Tentamos encontrar um endereço que já tenha esses novos dados ou criamos um novo.
            else {
                enderecoAlterado.setId(null);
                // OBS1: Quem chamar essa rotina deve se encarregar de:
                // 1) Desassociar a loja do enderecoOriginal;
                // 2) Associar a loja ao novoEnderecoUnico;
                // 3) Só depois, pedir aqui ao EnderecoDeCadastroService para limparSeOrfao (se o original ficou "órfao").
                return adicionarOuEncontarEnderecoDeEstoque(enderecoAlterado);
            }
        }
        
        // 5. Se IDs são diferentes, trata como uma troca de endereço (busca ou cria novo).
        else {
            // O mesmo que a OBS1. Deve acontecer sempre que entrar e a devolução tiver um id de endereco diferente do id do original.
            return adicionarOuEncontarEnderecoDeEstoque(enderecoAlterado);
        }
    }

    /**
     * Apaga um endereço de cadastro (exclusão lógica).
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void apagarEndereco(UUID id) {
        EnderecoDeEstoque endereco = obterPorID(id);

        // TODO: Verificar se este contato está vinculado a uma InformacaoDeRetirada antes de apaga-lo

        // Verifica se o endereço possui lojas vinculadas
        long lojasVinculadas = repository.contarLojasVinculadas(endereco.getId());
        if (lojasVinculadas > 0) {
            throw new AdvertenciaException("Não é possível apagar este endereço de estoque pois existem lojas vinculadas a ele.");
        }

        endereco.setHabilitado(Boolean.FALSE);
        repository.save(endereco);
    }

    /**
     * Verifica se um endereço não possui mais alguma loja vinculada a ele e faz a exclusão lógica.
     * 
     * @param idEndereco O ID do endereço.
     * @return true se o endereço foi excluído, false caso contrário.
     */
    @Transactional
    public boolean limparSeOrfao(UUID idEndereco) {
        Optional<EnderecoDeEstoque> optEndereco = repository.findById(idEndereco);

        if (optEndereco.isPresent()) {
            EnderecoDeEstoque endereco = optEndereco.get();

            long lojasVinculadas = repository.contarLojasVinculadas(endereco.getId());
            
            if (lojasVinculadas == 0) {
                endereco.setHabilitado(Boolean.FALSE);
                repository.save(endereco);
                return true; 
            }
        }
        return false; 
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoDeEstoque endereco = obterPorID(id);
        repository.delete(endereco);
    }
}