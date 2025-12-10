// src/main/java/br/com/ifpe/shopee/model/abstrato/contato/Contato.java

package br.com.ifpe.shopee.model.abstrato.contato;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Contato extends EntidadeAuditavelJPA {

	@NotBlank(message = "O valor do contato é obrigatório.")
    @Column(nullable = false)
	private String valor;
}
