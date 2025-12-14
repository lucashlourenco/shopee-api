// src/main/java/br/com/ifpe/shopee/model/bd_principal/request/contato/abstrato/ContatoBasicoRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.contato.abstrato;

import br.com.ifpe.shopee.model.enums.TipoDeContatoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class ContatoBasicoRequest extends ContatoRequest {
    
    private String nome;
    
    @NotNull(message = "O tipo de contato é obrigatório.")
    private TipoDeContatoEnum tipo;
}