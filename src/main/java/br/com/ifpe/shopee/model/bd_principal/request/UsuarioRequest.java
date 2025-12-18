// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/UsuarioRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioRequest {
    
    @NotBlank(message = "O login de contato (email/telefone) é obrigatório.")
    private String login;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}