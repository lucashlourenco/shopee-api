// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/StatusDeEnvio.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.enums.StatusDeEnvioEnum;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeNegocioJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
public class StatusDeEnvio extends EntidadeNegocioJPA{

	@NotBlank(message = "O status de envio é obrigatório.")
	@Column(nullable = false)
	private StatusDeEnvioEnum status;

	@CreatedDate
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dataDeRegistro;
	
	@Column
	private String observacao;

	@ManyToOne
	@JoinColumn(name = "id_envio")
	private Envio envio;
}
