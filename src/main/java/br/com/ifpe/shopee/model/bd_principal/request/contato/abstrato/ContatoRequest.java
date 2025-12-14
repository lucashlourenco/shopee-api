// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/contato/abstrato/ContatoRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.contato.abstrato;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class ContatoRequest {
    
    @NotBlank(message = "O valor do contato é obrigatório.")
    private String valor;
}
