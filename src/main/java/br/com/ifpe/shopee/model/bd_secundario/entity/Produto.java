// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Produto.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import br.com.ifpe.shopee.model.bd_principal.entity.Categoria;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "produto")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto extends EntidadeAuditavelData {

	private String nome;

	private StatusDeProdutoEnum status;

	private List<String> fotos;

	private String descricao;

	// Produto tem 1 categoria e categoria pode ser de muitos produtos
	@Transient	// Essa classe será usada para transporte de dados, logo é importante ter este objeto,
				// mas ele 'não pode' ser persistido no banco
	private Categoria categoria;
	// Referência JPA: O ID da Categoria
	@JsonIgnore // Não precisa devolver esta informação para o cliente (já terá o objeto inteiro na resposta)
	private UUID idCategoria;

	// Produto é de uma loja e loja tem muitos produtos
	@Transient	// Essa classe será usada para transporte de dados, logo é importante ter este objeto,
				// mas ele 'não pode' ser persistido no banco
	private Loja loja;
	// Referencia JPA: O ID da Loja
	@JsonIgnore // Não precisa devolver esta informação para o cliente (já terá o objeto inteiro na resposta)
	private UUID idLoja;

	// Um produto tem várias variacoes e a variação é de um produto
	@DBRef		// (lazy loading - carregamento lento - para evitar que o documento Produto muito grande)
	private List<Variacao> variacoes;

	// Um produto tem várias caracteristicas e a característica é de um produto
	@DBRef		// (lazy loading - carregamento lento - para evitar que o documento Produto muito grande)
	private List<Caracteristica> caracteristicas;
}

