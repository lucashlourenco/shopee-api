package br.com.ifpe.shopee.model.bd_principal.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InformacaoDeRetiradaRequest {

    @NotBlank(message = "O dia e horário são obrigatórios")
    private String diaEHorario;

    @NotNull(message = "O endereço de estoque é obrigatório")
    private UUID idEndereco;

    @NotEmpty(message = "Selecione pelo menos um contato")
    private List<UUID> idsContatos;
}