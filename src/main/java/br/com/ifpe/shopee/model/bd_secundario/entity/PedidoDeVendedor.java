// src/main/java/br/com/ifpe/shopee.model.bd_secundario.entity/PedidoDeVendedor.java
package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pedido_vendedor")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDeVendedor extends Pedido {

    // As cópias feitas via ItemDeCarrinho da superclasse

    // Vendedor (Loja) a quem este pedido pertence
    private UUID idVendedor;

    // Cliente que fez a compra
    private UUID idCliente;

    // Cópia do nome do cliente
    private String nomeDeCliente;

    // Cópia do contato do cliente que o vendedor tinha acesso
    private ContatoDeUsuario contatoDeCliente;
}