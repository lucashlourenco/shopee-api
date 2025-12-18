// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/LojaRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoComercialRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LojaRequest {

    @NotBlank(message = "O nome da loja é obrigatório.")
    private String nome;

    private String descricao;
    
    private String logo; 

    @Valid
    @NotNull(message = "O Endereço Comercial é obrigatório para a Loja.")
    private EnderecoComercialRequest enderecoComercial;

    public Loja build() {
        return Loja.builder()
                   .nome(nome)
                   .descricao(descricao)
                   .logo(logo)
                   // O Endereco é setado pelo service após ser gerenciado/persistido
                   .build();
    }
}