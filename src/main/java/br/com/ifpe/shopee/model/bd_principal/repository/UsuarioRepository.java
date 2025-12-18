// src/main/java/br/com/ifpe/shopee.model/bd_principal/repository/UsuarioRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    /**
     * Busca um Usuario pelo ID da Pessoa associada.
     * @param pessoaId O ID da Pessoa.
     * @return Usuario ou Optional.empty.
     */
    Optional<Usuario> findByPessoaId(UUID pessoaId);

    /**
     * Busca um Usuario através de um valor.
     * ATENÇÃO: O valor está independente de ContatoDeLogin (email, telefone),
     * mas a query usa o ContatoDeLogin (credenciais) no join. Se houver alterações
     * em ContatoDeLogin, essa consulta pode precisar ser atualizada, já que é
     * considerado um valor diretamente.
     * @param valor O valor (email ou telefone) do ContatoDeLogin.
     * @return Usuario ou Optional.empty.
     */
    @Query("SELECT u FROM Usuario u JOIN u.credenciais c WHERE c.valor = :valor AND c.habilitado = true")
    Optional<Usuario> findByLoginValor(@Param("valor") String valor);
}