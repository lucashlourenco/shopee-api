package br.com.ifpe.shopee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusDePagamentoEnum {
    EM_ANALISE("Em Análise"),
    CONFIRMADO("Confirmado"),
    RECUSADO("Recusado"),
    CANCELADO("Cancelado");

    private final String descricao;
}