// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Loja.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loja")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Loja extends EntidadeAuditavelJPA {

	@Column(nullable = false)
	@NotNull(message = "A loja precisa de um nome.")
	private String nome;

	@Column
	private String descricao;

	@Column
	private String logo;

	@OneToOne(mappedBy = "loja")
	private Vendedor vendedor;

	@ManyToOne
	@JoinColumn(name = "id_endereco")
	private EnderecoComercial endereco;

	@OneToMany(
		mappedBy = "loja", 
		orphanRemoval = true // Se um contato não for mais utilizado (não está associado a alguma loja), ele deve ser removido.
	)
	private List<ContatoDeLoja> contatos;

	@Transient
	private List<Produto> produtos;
}
