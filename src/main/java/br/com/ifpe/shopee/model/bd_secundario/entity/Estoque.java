// src/main/java/br/com/ifpe/shopee/model/bd_secundario/entity/Estoque.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class Estoque {

	private double preco;

	@Field("qtd_inicial")					// Definindo o nome do campo no banco
	private int qtdInicial;

	@Field("qtd_vendida")					// Definindo o nome do campo no banco
	private int qtdVendida;

	// Formatar em dd/MM/yyyy
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date dataFinal;

	// O estoque é de uma variacao e a variacao tem um estoque
	// O estoque será incorporado na variacao
}
