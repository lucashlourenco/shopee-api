// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/contato/ContatoDeUsuario.java

package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.abstrato.contato.ContatoBasico;

import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "contato_de_usuario")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDeUsuario extends ContatoBasico {

	@Column
	private boolean eVisivel;

	@ManyToOne
    @JoinColumn(name = "id_usuario")
	@JsonIgnore
	@ToString.Exclude
    private TipoDeUsuario usuario;

	@Override
    public int hashCode() {
        // Se tiver ID, usa o hash do ID. Se não, usa o hash semântico.
		if (getId() != null) {
			return getId().hashCode();
		}

		return Objects.hash(
			(getUsuario() != null ? getUsuario().getId() : null),
			getValor(),
			getTipo()
		);
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ContatoDeUsuario that = (ContatoDeUsuario) o;

		// Regra 1: Igualdade por Identidade (UUID)
		if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
			return true;
		}

		// Regra 2: Igualdade Semântica (mesmo Valor e Tipo de um mesmo Usuario)
		return	Objects.equals(getUsuario().getId(), that.getUsuario().getId()) &&
				Objects.equals(getValor(), that.getValor()) &&
				Objects.equals(getTipo(), that.getTipo());
	}
}
