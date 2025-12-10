// src/main/java/br/com/ifpe/shopee/model/abstrato/Pedido.java

package br.com.ifpe.shopee.model.abstrato;

import java.util.List;
import java.util.UUID;

import br.com.ifpe.shopee.model.bd_secundario.entity.StatusDePedido;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;

public abstract class Pedido extends EntidadeAuditavelData {

	private String observacao;

	private List<UUID> idsPagamento;

	// O Pedido tem muitos StatusDePedido e um StatusDePedido é de um Pedido
	private List<StatusDePedido> status;

	// Relativo ao produto
	// Este já vai ficar fixo no bd_pincipal juntamente com o envio e status de envio
    private UUID idItemDeCarrinho;
}
