// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/contato/ContatoDeLojaRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.contato;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.request.contato.abstrato.ContatoBasicoRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder; // ALTERADO: De @Builder para @SuperBuilder

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ContatoDeLojaRequest extends ContatoBasicoRequest {

    /**
     * Converte o Request (DTO) para a Entidade de Persistência,
     * usando setters para popular campos herdados.
     */
    public ContatoDeLoja build() {
        ContatoDeLoja contato = new ContatoDeLoja();
        
        contato.setValor(getValor());
        contato.setNome(getNome());
        contato.setTipo(getTipo());
        
        return contato;
    }
}