// src/main/java/br/com/ifpe/shopee.model.bd_secundario.entity/StatusDePedido.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.time.LocalDateTime;
import br.com.ifpe.shopee.model.enums.StatusDePedidoEnum;
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
public class StatusDePedido {

    // Incorporado no Pedido (poderá ser no PedidoDeVendedor ou PedidoDeCliente)

    private StatusDePedidoEnum status;

    @Builder.Default
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataDeRegistro = LocalDateTime.now();
}