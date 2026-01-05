// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/contato/ContatoDeLogin.java

package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.model.abstrato.contato.Contato;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "contato_de_login")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDeLogin extends Contato {

    @ManyToOne
    @JoinColumn(name = "id_usuario")
	@JsonIgnore
	@ToString.Exclude
    private Usuario usuario;

    @Override
    public int hashCode() {
        // Se tiver ID, usa o hash do ID. Se não, usa o hash do Valor.
        return (getId() != null) ? getId().hashCode() : (getValor() != null ? getValor().hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ContatoDeLogin that = (ContatoDeLogin) o;

        // Regra 1: Igualdade por Identidade (UUID)
        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        // Regra 2: Igualdade Semântica (mesmo Valor)
        return this.getValor() != null && this.getValor().equals(that.getValor());
    }
}
