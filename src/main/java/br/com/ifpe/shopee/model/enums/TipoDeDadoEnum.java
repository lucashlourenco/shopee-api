package br.com.ifpe.shopee.model.enums;

public enum TipoDeDadoEnum {

	TEXTO("Texto"),
	NUMERO_INT("Número inteiro"),
    NUMERO_REAL("Número real"),;

	private final String descricao;

    private TipoDeDadoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
