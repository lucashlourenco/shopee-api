package br.com.ifpe.shopee.model.enums;

public enum EstadoDeProdutoEnum {

	NOVO("Novo"),
	USADO("Usado"),
    RECONDICIONADO("Recondicionado");

	private final String descricao;

    private EstadoDeProdutoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}