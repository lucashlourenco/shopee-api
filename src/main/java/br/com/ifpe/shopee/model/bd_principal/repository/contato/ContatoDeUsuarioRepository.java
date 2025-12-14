// src/main/java/br/com/ifpe/shopee/model/bd_principal/repository/contato/ContatoDeUsuarioRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.contato;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.enums.TipoDeContatoEnum;

public interface ContatoDeUsuarioRepository extends JpaRepository<ContatoDeUsuario, UUID> {
    
    /**
     * Consulta para buscar todos os contatos de um Usuário específico pelo seu ID.
     */
    List<ContatoDeUsuario> findByUsuarioId(UUID idUsuario);

    /**
     * Consulta para verificar a unicidade de domínio: 
     * um usuário não pode ter o mesmo Valor e Tipo de Contato duas vezes.
     */
    ContatoDeUsuario findByUsuarioIdAndValorAndTipo(UUID idUsuario, String valor, TipoDeContatoEnum tipo);
}