package br.com.ifpe.shopee.model.bd_secundario.request;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagamentoRequest {

    @NotNull(message = "O ID do pedido é obrigatório")
    private UUID idPedido;

    @Valid
    @NotNull(message = "Detalhes do pagamento são obrigatórios")
    private DetalhesRequest detalhes;

    private String observacao;

    @Data
    public static class DetalhesRequest {
        @NotNull(message = "O valor é obrigatório")
        @Min(value = 0, message = "O valor deve ser positivo")
        private Double valor;

        @NotBlank(message = "Forma de pagamento obrigatória")
        private String formaDePagamento;

        @Min(1)
        private Integer parcelas = 1;
    }
}