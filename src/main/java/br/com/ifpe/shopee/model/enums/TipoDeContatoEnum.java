package br.com.ifpe.shopee.model.enums;

public enum TipoDeContatoEnum {
    
    // 1. Definição das constantes com seus valores
    EMAIL("Email"),
    TELEFONE("Telefone"),
    CELULAR("Celular"),
    WHATSAPP("WhatsApp");

    // 2. Campo privado e final para armazenar o valor
    private final String descricao;

    // 3. Construtor para inicializar o valor da constante
    private TipoDeContatoEnum(String descricao) {
        this.descricao = descricao;
    }

    // 4. Método getter público para acessar o valor (String)
    public String getDescricao() {
        return descricao;
    }
}
