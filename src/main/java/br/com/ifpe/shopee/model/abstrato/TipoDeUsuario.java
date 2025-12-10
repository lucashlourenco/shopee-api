// src/main/java/br/com/ifpe/shopee/model/abstrato/TipoDeUsuario.java

package br.com.ifpe.shopee.model.abstrato;

import java.util.List;

import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_de_usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Unifica em uma tabela
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING) // Coluna para saber qual tipo é
public abstract class TipoDeUsuario extends EntidadeAuditavelJPA {

	@Column
	private String nome;

	@ManyToOne
	// @JoinColumn(name = "id_usuario", nullable = false)						|> Ruinm para a compra
	// @NotNull(message = "Este tipo precisa estar associado a um usuário.")	|
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@OneToMany(mappedBy = "usuario")
	private List<ContatoDeUsuario> contatos;
}
