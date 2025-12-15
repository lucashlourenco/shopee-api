// src/main/java/br.com.ifpe.shopee.model/bd_principal/request/endereco/EnderecoDeCadastroRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.endereco;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.request.endereco.abstrato.EnderecoRequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EnderecoDeCadastroRequest extends EnderecoRequest {

    /**
     * Converte o Request (DTO) para a Entidade de Persistência.
     */
    public EnderecoDeCadastro build() {
        EnderecoDeCadastro endereco = new EnderecoDeCadastro();
        
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