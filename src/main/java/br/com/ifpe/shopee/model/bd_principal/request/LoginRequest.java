// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/LoginRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    
    @NotBlank(message = "O login (email ou telefone) é obrigatório.")
    private String login;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}