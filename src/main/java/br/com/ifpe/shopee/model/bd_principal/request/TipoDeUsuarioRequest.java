// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/TipoDeUsuarioRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Como só o nome pode ser alterado em um TipoDeUsuario, temos apenas isso
public class TipoDeUsuarioRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;
}