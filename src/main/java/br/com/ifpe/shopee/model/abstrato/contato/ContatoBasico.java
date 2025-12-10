// src/main/java/br/com/ifpe/shopee/model/abstrato/contato/ContatoBasico.java

package br.com.ifpe.shopee.model.abstrato.contato;

import br.com.ifpe.shopee.model.enums.TipoDeContatoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ContatoBasico extends Contato {

	@Column
	private String nome;

	@NotNull(message = "O tipo de contato é obrigatório.")
    @Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TipoDeContatoEnum tipo;
}
