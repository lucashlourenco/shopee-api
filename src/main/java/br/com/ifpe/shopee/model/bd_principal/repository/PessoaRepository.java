// src/main/java/br/com/ifpe/shopee.model/bd_principal/repository/PessoaRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    
    /**
     * Busca uma Pessoa pelo seu CPF.
     * @param cpf O número de CPF.
     * @return Pessoa ou Optional.empty se não existir.
     */
    Optional<Pessoa> findByCpf(String cpf);
    
    /**
     * Verifica se existe uma Pessoa com um determinado CPF.
     * @param cpf O número de CPF.
     * @return true se o CPF já estiver cadastrado.
     */
    boolean existsByCpf(String cpf);

    /**
     * Conta quantos Usuários ATIVOS estão vinculados a esta Pessoa.
     * O filtro habilitado = true é garantido pelo @SQLRestriction na entidade Usuario.
     * @param pessoaId O ID da Pessoa.
     * @return O número de Usuários ativos.
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.pessoa.id = :pessoaId")
    long countUsuariosAtivosByPessoaId(@Param("pessoaId") UUID pessoaId);
}