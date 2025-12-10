// src/main/java/br/com/ifpe/shopee/model/bd_secundario/entity/Variacao.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.enums.EstadoDeProdutoEnum;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "variacao")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Variacao extends EntidadeAuditavelData {

	private EstadoDeProdutoEnum estado;

	private String nome;

	private StatusDeProdutoEnum status;

	private List<String> fotos;

	private String descricao;

	private double peso;

	@Field("tamanho_envio_x")
	private double tamanoDeEnvioX;

	@Field("tamanho_envio_y")
	private double tamanoDeEnvioY;

	@Field("tamanho_envio_z")
	private double tamanoDeEnvioZ;

	// variacao tem um estoque e o estoque é de uma variacao
	// O Estoque é aninhado (incorporado) no documento Variacao.
	// Não precisa de anotação, é o comportamento padrão do Spring Data Mongo.
	private Estoque estoque;

	// Uma variacao tem um conjunto de caracteristicas e as caracteristicas pertencem a uma variacao
	@DBRef
	private List<Caracteristica> caracteristicas;

	// A variacao é de um produto e o produto tem várias variacao
	// Estratégia de referência bidireccional manual para evitar o ciclo de referência
	@Transient
	private Produto produto;
	@JsonIgnore
	private UUID idProduto; // Persiste no Mongo

	// Uma variacao pode estar em diferntes ítens de carrinho e o ítem de carrinho tem uma variacao
	// Não será bidireccional. Apeas o item de carrinho terá uma variacao.
	// List<ItemDeCarrinho> itensDeCarrinho não existirá

	// Uma variacao tem um endereço de estoque e o endereço de estoque pode conter várias variacao
	@Transient
	private EnderecoDeEstoque enderecoDeEstoque;
	@JsonIgnore
	private UUID idEnderecoDeEstoque;

	// Uma variacao tem uma informação de retirada e a informação de retirada pode ser de várias variacao
	@Transient
	private InformacaoDeRetirada informacaoDeRetirada;
	@JsonIgnore
	private UUID idInformacaoDeRetirada;
}
