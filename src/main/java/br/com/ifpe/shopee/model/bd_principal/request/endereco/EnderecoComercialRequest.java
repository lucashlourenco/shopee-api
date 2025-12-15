// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/endereco/EnderecoComercialRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.endereco;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.abstrato.EnderecoRequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EnderecoComercialRequest extends EnderecoRequest {

    /**
     * Converte o Request (DTO) para a Entidade de Persistência.
     */
    public EnderecoComercial build() {
        EnderecoComercial endereco = new EnderecoComercial();
        
        endereco.setCep(getCep());
        endereco.setRua(getRua());
        endereco.setBairro(getBairro());
        endereco.setCidade(getCidade());
        endereco.setEstado(getEstado());
        endereco.setNumero(getNumero());
        endereco.setComplemento(getComplemento());
        endereco.setReferencia(getReferencia());

        return endereco;
    }
}