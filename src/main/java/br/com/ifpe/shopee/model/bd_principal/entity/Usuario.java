// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Usuario.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario extends EntidadeAuditavelJPA {

	@OneToMany(mappedBy = "usuario")
	private List<ContatoDeLogin> credenciais;

	@JsonIgnore
	@Column(nullable = false)
	@NotNull(message = "A senha é obrigatória.")
	private String senha;

	@ManyToOne
	@JoinColumn(name = "id_pessoa", nullable = false)
	@NotNull(message = "O usuário precisa está associado a uma pessoa.")
	private Pessoa pessoa;

	// TODO: Considerar mudar para OneToOne. 
	@OneToMany(mappedBy = "usuario")
	private List<TipoDeUsuario> tipos;

}
