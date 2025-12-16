package br.com.ifpe.shopee.model.bd_principal.request;

import br.com.ifpe.shopee.model.enums.TipoDeDadoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TipoDeCaracteristicaRequest {

    @NotBlank(message = "O nome da característica é obrigatório (Ex: Cor, Voltagem)")
    private String nome;

    @NotNull(message = "O tipo de dado é obrigatório")
    private TipoDeDadoEnum tipoDeDado;

    private boolean eObrigatoria;
}