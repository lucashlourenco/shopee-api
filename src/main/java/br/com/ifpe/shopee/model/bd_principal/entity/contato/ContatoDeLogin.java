// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/contato/ContatoDeLogin.java

package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.contato.Contato;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

@Entity
@Table(
    name = "contato_de_login",
    // Adiciona uma restrição de unicidade na coluna 'valor' para garantir que o contato seja exclusivo
    uniqueConstraints = @UniqueConstraint(columnNames = {"valor"}) 
)
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDeLogin extends Contato {

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
