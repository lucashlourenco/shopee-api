// src/main/java/br.com.ifpe.shopee.model/bd_principal/repository/endereco/EnderecoComercialrepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;

public interface EnderecoComercialRepository extends JpaRepository<EnderecoComercial, UUID> {
    
    /**
     * Consulta para buscar um endereço pela sua unicidade de domínio (EnderecoComercial):
     * lojas que tiverem o mesmo cep, rua, bairro, cidade, estado, numero e complemento, terão o mesmo endereço comercial.
     */
    Optional<EnderecoComercial> findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplemento (
        String cep,
        String rua,
        String bairro,
        String cidade,
        String estado,
        String numero, 
        String complemento
    );
}