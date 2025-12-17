package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.UUID;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pedido")
@TypeAlias("PedidoDeVendedor")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDeVendedor extends Pedido {

    private UUID idPedidoClientePai;
    private UUID idVendedor;
    private UUID idCliente;
    private String nomeDeCliente;
    private ContatoDeUsuario contatoDeCliente;
    private Double valorTotalVendedor;

    // --- SNAPSHOT DE ENVIO (ADICIONADO) ---
    // Garante o histórico do frete mesmo após limpeza do carrinho
    private String transportadora;
    private Double valorFrete;
    private Integer prazoEntregaDias;
    private String codigoRastreio; 
}