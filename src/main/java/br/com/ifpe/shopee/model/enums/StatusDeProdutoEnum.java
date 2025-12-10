package br.com.ifpe.shopee.model.enums;

public enum StatusDeProdutoEnum {

	EM_TESTE("Em Teste"),
	ATIVO("Ativo"),
	PAUSADO("Pausado"),
	CANCELADO("Cancelado");

	private final String descricao;

    private StatusDeProdutoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
