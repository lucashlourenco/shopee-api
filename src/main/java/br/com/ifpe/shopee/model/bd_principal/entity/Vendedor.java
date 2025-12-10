// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Vendedor.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_secundario.entity.PedidoDeVendedor;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SQLRestriction("habilitado = true")
@DiscriminatorValue("Vendedor")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vendedor extends TipoDeUsuario {

	@Transient
    private List<PedidoDeVendedor> vendas;

	@Column
	private String cnpj;

	@OneToOne
	@JoinColumn(name = "id_loja", unique = true)
	private Loja loja;
}
