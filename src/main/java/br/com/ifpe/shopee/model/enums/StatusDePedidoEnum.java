package br.com.ifpe.shopee.model.enums;

public enum StatusDePedidoEnum {

	EM_ANALISE("Em Análise"),
	A_CONFIRMAR("A Confirmar"),
	AGUARDANDO_ENVIO("Aguardando Envio"),
	ENVIO_PARCIAL("Envio Parcial"),
	EM_TRANSITO("Em Transito"),
	CONCLUSAO_PARCIAL("Conclusão Parcial"),
	CONCLUIDO("Concluído"),
	EM_DISPUTA("Em Disputa"),
	CANCELAMENTO_PARCIAL("Cancelamento Parcial"),
	CANCELADO("Cancelado");
	
	private final String descricao;

    private StatusDePedidoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
