// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/contato/ContatoDeUsuario.java

package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.abstrato.contato.ContatoBasico;

import org.hibernate.annotations.SQLRestriction;

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
    private TipoDeUsuario usuario;
}
