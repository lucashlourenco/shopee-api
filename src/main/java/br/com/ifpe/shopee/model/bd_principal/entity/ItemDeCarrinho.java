// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/ItemDeCarrinho.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeNegocioJPA;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_de_carrinho")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDeCarrinho extends EntidadeNegocioJPA {
	
	@Column
	private int quantidade;

	@Column
	private String observacao;

    @OneToOne
    @JoinColumn(name = "id_envio", unique = true)
    private Envio envio;

	@ManyToOne
	@JoinColumn(name = "id_carrinho")
	private CarrinhoDeCompra carrinho;

	@Transient
	private Variacao variacao;

	// TODO: Lógica da compra -
	// 1) ItemDeCarrinho perde a ligação com o CarrinhoDeCompra
	// 2) Salva um ProdutoComprado - (cópia de todas as informações asscociadas a variação comprada; as informações de variação produto,
	// características, estoque e InformaçãoDeRetirada - com seus contatos - também estarão no ProdutoComprado
	// criado) para registro permanente


	// Relativo ao produto
    // Servirá de link para a variação comprada (pelo id) ainda que ela possa mudar suas informações
	@Column(name = "id_variacao")
    private UUID idVariacao;
	// Cópia de todas as informações asscociadas a variação comprada
	@Column(name = "id_produto_comprado")
	private UUID idProdutoComprado;
}
