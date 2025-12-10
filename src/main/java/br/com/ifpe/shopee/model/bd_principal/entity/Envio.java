// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Envio.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "envio")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Envio extends EntidadeAuditavelJPA{

	@Column
	private double preco;

	@Column
	private String transportadora;

	@Column
	private int prazoDeEntrega;

	@Column
	private String codigoDeRastreio;

	@OneToMany(mappedBy = "envio")
	private List<StatusDeEnvio> statusDeEnvios;

	@OneToOne(mappedBy = "envio")
	private ItemDeCarrinho itemDeCarrinho;
}
