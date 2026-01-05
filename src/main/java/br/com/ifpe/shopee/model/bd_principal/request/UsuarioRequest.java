// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/UsuarioRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import br.com.ifpe.shopee.model.bd_principal.request.contato.ContatoDeLoginRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioRequest {
    
    @Valid // Garante que as anotações dentro de ContatoDeLoginRequest sejam validadas
    @NotNull(message = "Os dados de login são obrigatórios.")
    private ContatoDeLoginRequest login;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}