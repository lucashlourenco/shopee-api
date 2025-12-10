// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Pessoa.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name = "pessoa")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pessoa extends EntidadeAuditavelJPA {

	@NotBlank(message = "O nome é obrigatório.")
	@Column(nullable = false)
	private String nomeCompleto;

	@NotBlank(message = "O CPF é obrigatório.")
	@Column(nullable = false)
	private String cpf;

	@Column
	private String nacionalidade;

	@Column(name = "data_nascimento")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataNascimento;

	@ManyToOne
	@JoinColumn(name = "id_endereco")
	private EnderecoDeCadastro endereco;

	@OneToMany(mappedBy = "pessoa")
	private List<Usuario> usuarios;
}
