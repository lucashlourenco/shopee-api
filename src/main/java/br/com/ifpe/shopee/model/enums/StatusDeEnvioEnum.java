package br.com.ifpe.shopee.model.enums;

public enum StatusDeEnvioEnum {

	EM_PROCESSAMENTO("Em Processamento"),
	AGUARDANDO_COLETA("Aguardando Coleta"),
	EM_TRANSITO("Em Transito"),
	CONCLUIDO("Concluído"),
	EM_DISPUTA("Em Disputa"),
	CANCELADO("Cancelado");

	private final String descricao;

    private StatusDeEnvioEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
