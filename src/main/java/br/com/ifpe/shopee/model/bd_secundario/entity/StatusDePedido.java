package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.enums.StatusDePedidoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusDePedido {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private StatusDePedidoEnum status;

    @Builder.Default
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataDeRegistro = LocalDateTime.now();
    
    private String observacao;

    // Método utilitário para facilitar a criação no Service
    public static StatusDePedido criar(StatusDePedidoEnum status, String observacao) {
        return StatusDePedido.builder()
            .status(status)
            .observacao(observacao)
            .build(); // Data e ID são gerados automaticamente pelo @Builder.Default
    }
}