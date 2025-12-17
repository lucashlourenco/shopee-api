package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusDePagamento {
    
    // Diagrama pede ID string
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private StatusDePagamentoEnum status;

    @Builder.Default
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataDeRegistro = LocalDateTime.now();
    
    private String observacao;
}