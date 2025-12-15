// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/endereco/abstrato/EnderecoRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.endereco.abstrato;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public abstract class EnderecoRequest {
    
    // Apenas os campos do Endereco.java
    @NotBlank(message = "O CEP é obrigatório.")
    private String cep;

    @NotBlank(message = "O nome da rua é obrigatório.")
    private String rua;

    @NotBlank(message = "O nome do bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "O nome da cidade é obrigatório.")
    private String cidade;

    @NotBlank(message = "O nome do estado é obrigatório.")
    private String estado;

    @NotBlank(message = "O numero do endereço é obrigatório.")
    private String numero;

    private String complemento;

    private String referencia;
}