// src/main/java/br/com/ifpe/shopee/model/bd_principal/request/CadastroVendedorRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CadastroVendedorRequest {
    
    // 1. Dados da Pessoa (Identidade)
    @Valid
    @NotNull(message = "Os dados da pessoa são obrigatórios.")
    private PessoaRequest pessoa; 

    // 2. Dados da Conta (Login/Senha)
    @Valid
    @NotNull(message = "Os dados da conta (login/senha) são obrigatórios.")
    private UsuarioRequest conta;

    // 3. Dados Específicos do Vendedor
    private String cnpj;

    @Valid
    private LojaRequest loja;
}