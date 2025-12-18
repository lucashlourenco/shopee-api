// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/cliente/CadastroClienteRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoDeEntregaRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CadastroClienteRequest {

    // Contém os dados civis e o EnderecoDeCadastro
    @Valid
    @NotNull(message = "Os dados da pessoa são obrigatórios.")
    private PessoaRequest pessoa; 

    // Contém login e senha
    @Valid
    @NotNull(message = "Os dados da conta (login/senha) são obrigatórios.")
    private UsuarioRequest conta;
    
    // Endereço de Entrega é opcional no cadastro inicial
    @Valid
    private EnderecoDeEntregaRequest enderecoDeEntregaInicial; 
}