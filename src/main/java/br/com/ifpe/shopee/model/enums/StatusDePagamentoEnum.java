package br.com.ifpe.shopee.model.enums;

public enum StatusDePagamentoEnum {

	EM_ANALISE("Em Análise"),
	CONFIRMADO("Confirmado"),
	RECUSADO("Recusado"),
	CANCELADO("Cancelado");

	private final String descricao;

    private StatusDePagamentoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}