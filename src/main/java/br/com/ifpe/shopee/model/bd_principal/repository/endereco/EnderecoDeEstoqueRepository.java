// src/main/java/br.com.ifpe.shopee.model/bd_principal/repository/endereco/EnderecoDeEstoqueRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;

public interface EnderecoDeEstoqueRepository extends JpaRepository<EnderecoDeEstoque, UUID> {
    
    /**
     * Consulta para buscar um endereço pela sua unicidade semântica de endereço de estoque:
     * Endereços que tenham os mesmos cep, rua, bairro, cidade, estado, numero e complemento e nome, são iguais.
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
}