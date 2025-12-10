// src/main/java/br/com/ifpe/shopee/model/bd_principal/Categoria.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeNegocioJPA;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categoria extends EntidadeNegocioJPA {

	@NotBlank(message = "O nome é obrigatório.")
	@Column(nullable = false)
	private String nome;

	@Column
	private boolean estaAtiva;

	// Mapeamento de lista de tipos básicos (String)
	@ElementCollection
	@CollectionTable(name = "categoria_marcas_reconhecidas", joinColumns = @JoinColumn(name = "id_categoria"))
	@Column(name = "marca_reconhecida") // Nome da coluna na nova tabela
	private List<String> marcasReconhecidas;

    @ManyToOne // Uma categoria filha tem um único pai
    @JoinColumn(name = "id_categoria_pai")
    private Categoria pai;

    @OneToMany(mappedBy = "pai") // O relacionamento é mapeado pelo campo "pai" na entidade Categoria
    private List<Categoria> filhas;

	@ManyToMany
	@JoinTable(
    	name = "categoria_tipo_caracteristica", // Nome da tabela de junção
    	joinColumns = @JoinColumn(name = "id_categoria"),
    	inverseJoinColumns = @JoinColumn(name = "id_tipo_caracteristica")
	)
	private List<TipoDeCaracteristica> caracteristicas;
}
