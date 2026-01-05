// src/main/java/br.com.ifpe.shopee.model/bd_principal/repository/endereco/EnderecoDeEstoqueRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.endereco;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;

public interface EnderecoDeEstoqueRepository extends JpaRepository<EnderecoDeEstoque, UUID> {

    /**
     * Consulta para buscar um endereço pela sua unicidade semântica de endereço de estoque:
     * Endereços que tenham os mesmos cep, rua, bairro, cidade, estado, numero, complemento e nome são iguais.
     */
    Optional<EnderecoDeEstoque> findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplementoAndNome(
        String cep,
        String rua,
        String bairro,
        String cidade,
        String estado,
        String numero, 
        String complemento,
        String nome
    );
    
    /**
     * Conta quantas lojas estão vinculadas a este endereço de estoque via SQL.
     */
    @Query("SELECT COUNT(l) FROM Loja l JOIN l.depositos d WHERE d.id = :idEndereco AND l.habilitado = true")
    long contarLojasVinculadas(@Param("idEndereco") UUID idEndereco);

    /**
     * Busca todos os estoques onde a lista de lojas contenha o ID informado.
     */
    List<EnderecoDeEstoque> findByLojasId(UUID idLoja);
}