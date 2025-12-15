// src/main/java/br/com/ifpe/shopee/model/abstrato/Endereco.java

package br.com.ifpe.shopee.model.abstrato;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Endereco extends EntidadeAuditavelJPA {

	@NotBlank(message = "O CEP é obrigatório.")
    @Column(nullable = false)
	private String cep;

	@NotBlank(message = "O nome da rua é obrigatório.")
    @Column(nullable = false)
	private String rua;

	@NotBlank(message = "O nome do bairro é obrigatório.")
    @Column(nullable = false)
	private String bairro;

	@NotBlank(message = "O nome da cidade é obrigatório.")
    @Column(nullable = false)
	private String cidade;

	@NotBlank(message = "O nome do estado é obrigatório.")
    @Column(nullable = false)
	private String estado;

	@NotBlank(message = "O numero do endereço é obrigatório.")
    @Column(nullable = false)
	private String numero;

	@Column
	private String complemento;

	// Ponto de referência para o endereço.
	@Column
	private String referencia;
}
