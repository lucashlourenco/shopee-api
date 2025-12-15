// src/main/java/br.com.ifpe.shopee.model/bd_principal/request/endereco/EnderecoDeEstoqueRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.endereco;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.abstrato.EnderecoRequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EnderecoDeEstoqueRequest extends EnderecoRequest {

    private String nome;

    /**
     * Converte o Request (DTO) para a Entidade de Persistência.
     */
    public EnderecoDeEstoque build() {
        EnderecoDeEstoque endereco = new EnderecoDeEstoque();
        
        endereco.setCep(getCep());
        endereco.setRua(getRua());
        endereco.setBairro(getBairro());
        endereco.setCidade(getCidade());
        endereco.setEstado(getEstado());
        endereco.setNumero(getNumero());
        endereco.setComplemento(getComplemento());
        endereco.setReferencia(getReferencia());
        
        // Campo específico
        endereco.setNome(nome); 

        return endereco;
    }
}