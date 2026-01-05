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
     * Verifica se uma Pessoa possui algum usuário ativo.
     * 
     * @param pessoaId O ID da Pessoa.
     * @return true se a Pessoa possui um usuário ativo.
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.pessoa.id = :pessoaId AND u.habilitado = true")
    boolean existeUsuarioAtivoParaPessoa(@Param("pessoaId") UUID pessoaId);
}