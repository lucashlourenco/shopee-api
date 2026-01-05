// src/main/java/br.com.ifpe.shopee.model/bd_principal/repository/endereco/EnderecoDeEntregaRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.endereco;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;

public interface EnderecoDeEntregaRepository extends JpaRepository<EnderecoDeEntrega, UUID> {
    
    /**
     * Busca todos os endereços de entrega associados a um Cliente específico.
     * @param idCliente O ID do Cliente.
     * @return Lista de EnderecoDeEntrega.
     */
    List<EnderecoDeEntrega> findByClienteId(UUID idCliente);

    /**
     * Consulta para buscar um endereço pela sua unicidade semântica de endereço de entrega:
     * Endereços que tenham os mesmos cep, rua, bairro, cidade, estado, numero, complemento e nome são iguais.
     */
    Optional<EnderecoDeEntrega> findByCepAndRuaAndBairroAndCidadeAndEstadoAndNumeroAndComplementoAndNome(
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