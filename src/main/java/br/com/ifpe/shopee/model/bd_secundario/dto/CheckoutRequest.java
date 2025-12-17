package br.com.ifpe.shopee.model.bd_secundario.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull(message = "O ID do carrinho é obrigatório")
    private UUID idCarrinho;

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento; // Ex: "PIX", "CREDITO"
    
    // Futuro: private String cupomDesconto;
}