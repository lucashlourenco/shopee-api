// src/main/java/br/com/ifpe/shopee.model.bd_secundario.entity/Pagamento.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pagamento")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento extends EntidadeAuditavelData {
	private String observacao;

	private RespostaDePagamento detalhes;
	
	private List<StatusDePagamento> status;

    @DBRef // Relaciona Pagamento com PedidoDeCliente
	private List<PedidoDeCliente> pedidos; 
}