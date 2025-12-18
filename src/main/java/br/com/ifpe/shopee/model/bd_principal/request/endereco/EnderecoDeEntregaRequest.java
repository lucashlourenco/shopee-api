// src/main/java/br.com.ifpe.shopee.model/bd_principal/request/endereco/EnderecoDeEntregaRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.endereco;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.abstrato.EnderecoRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EnderecoDeEntregaRequest extends EnderecoRequest {

    @NotBlank(message = "O nome do endereço (apelido) é obrigatório.")
    private String nome;

    @NotBlank(message = "O nome do recebedor é obrigatório.")
    private String recebedor;

    /**
     * Converte o Request (DTO) para a Entidade de Persistência.
     */
    public EnderecoDeEntrega build() {
        EnderecoDeEntrega endereco = new EnderecoDeEntrega();
        
        endereco.setCep(getCep());
        endereco.setRua(getRua());
        endereco.setBairro(getBairro());
        endereco.setCidade(getCidade());
        endereco.setEstado(getEstado());
        endereco.setNumero(getNumero());
        endereco.setComplemento(getComplemento());
        endereco.setReferencia(getReferencia());
        
        // Campos específicos
        endereco.setNome(nome); 
        endereco.setRecebedor(recebedor);

        return endereco;
    }
}