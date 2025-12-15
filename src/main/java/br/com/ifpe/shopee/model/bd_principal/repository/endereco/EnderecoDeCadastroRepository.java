// src/main/java/br.com.ifpe.shopee.model/bd_principal/repository/endereco/EnderecoDeCadastroRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.endereco;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;

public interface EnderecoDeCadastroRepository extends JpaRepository<EnderecoDeCadastro, UUID> {
    
    /**
     * Consulta para buscar um endereço pela sua unicidade de domínio (EnderecoComercial):
     * Pessoas que tiverem o mesmo cep, rua, bairro, cidade, estado, numero e complemento, terão o mesmo endereço de cadastro.
     */
    Optional<EnderecoDeCadastro> findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplemento(
        String cep,
        String rua,
        String bairro,
        String cidade,
        String estado,
        String numero, 
        String complemento
    );
}