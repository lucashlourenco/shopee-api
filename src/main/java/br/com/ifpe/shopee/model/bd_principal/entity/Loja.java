// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Loja.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
import lombok.ToString;

@Entity
@Table(name = "loja")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"vendedor", "endereco", "contatos", "depositos"})
public class Loja extends EntidadeAuditavelJPA {

	@Column(nullable = false)
	@NotNull(message = "A loja precisa de um nome.")
	private String nome;

	@Column
	private String descricao;

	@Column
	private String logo;

	@OneToOne(mappedBy = "loja")
	@JsonIgnore
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

	@ManyToMany
	@JoinTable(
		name = "loja_endereco_de_estoque",
		joinColumns = @JoinColumn(name = "id_loja"),
		inverseJoinColumns = @JoinColumn(name = "id_endereco_de_estoque")
	)
	private List<EnderecoDeEstoque> depositos;

	// Getters personalizados para o JSON da Loja, já que não podemos vazar os dados completos de Vendedor em consultas públicas

	@JsonProperty("nomeDeVendedor")
    public String getNomeDeVendedor() {
        return vendedor != null ? vendedor.getNome() : null;
    }

	@JsonProperty("cnpjDeVendedor")
    public String getCnpjDeVendedor() {
        return vendedor != null ? vendedor.getCnpj() : null;
    }
	
    @JsonProperty("dataDeCadastroDeVendedor")
    @JsonFormat(pattern = "dd/MM/yyyy")
    public LocalDateTime dataDeCadastroDeVendedor() {
        return vendedor != null ? vendedor.getDataDeCadastro() : null;
    }
}
