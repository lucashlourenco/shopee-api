// src/main/java/br/com/ifpe/shopee.model.bd_secundario.entity/StatusDePagamento.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusDePagamento {

	private StatusDePagamentoEnum status;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Builder.Default											// Habilitar o preenchimento com um valor padrão
	private LocalDateTime dataDeRegistro = LocalDateTime.now();	// Definindo um valor padrão para o atributo
}