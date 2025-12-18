// src/main/java/br/com/ifpe/shopee.model/bd_principal/repository/TipoDeUsuarioRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;

// Usamos TipoDeUsuario para consultas na tabela única.
public interface TipoDeUsuarioRepository extends JpaRepository<TipoDeUsuario, UUID> {
    
    /**
     * Verifica se uma Pessoa já possui um papel de Cliente.
     * @param idPessoa ID da Pessoa.
     * @return True se um Cliente associado à Pessoa existir.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c JOIN c.usuario u WHERE u.pessoa.id = :idPessoa")
    boolean existsClienteByPessoaId(@Param("idPessoa") UUID idPessoa);

    /**
     * Verifica se uma Pessoa já possui um papel de Vendedor.
     * @param idPessoa ID da Pessoa.
     * @return True se um Vendedor associado à Pessoa existir.
     */
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vendedor v JOIN v.usuario u WHERE u.pessoa.id = :idPessoa")
    boolean existsVendedorByPessoaId(@Param("idPessoa") UUID idPessoa);

    /**
     * Contagem de Tipos de Usuários ativos vinculados a um Usuário.
     * @param usuarioId ID do Usuário.
     * @return Quantidade de Tipos de Usuários ativos.
     */
    @Query("SELECT COUNT(t) FROM TipoDeUsuario t WHERE t.usuario.id = :usuarioId")
    long countTiposAtivosByUsuarioId(@Param("usuarioId") UUID usuarioId);
}