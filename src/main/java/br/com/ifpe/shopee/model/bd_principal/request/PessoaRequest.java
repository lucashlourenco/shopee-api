// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/PessoaRequest.java

package br.com.ifpe.shopee.model.bd_principal.request;

import java.util.Date;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.EnderecoDeCadastroRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PessoaRequest {
    
    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    private String nacionalidade;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dataNascimento;

    @Valid
    @NotNull(message = "O Endereço de Cadastro é obrigatório.")
    private EnderecoDeCadastroRequest enderecoDeCadastro;

    /**
     * Converte o Request (DTO) para a Entidade de Persistência Pessoa.
     */
    public Pessoa build() {
        return Pessoa.builder()
                     .nomeCompleto(nomeCompleto)
                     .cpf(cpf)
                     .nacionalidade(nacionalidade)
                     .dataNascimento(dataNascimento)
                     // O EnderecoDeCadastro é injetado pelo service, não pelo request
                     .build();
    }
}