// src/main/java/br/com/ifpe/shopee.model/bd_principal/request/contato/ContatoDeUsuarioRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.contato;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.request.contato.abstrato.ContatoBasicoRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ContatoDeUsuarioRequest extends ContatoBasicoRequest {

    private boolean eVisivel;

    /**
     * Converte o Request (DTO) para a Entidade de Persistência.
     */
    public ContatoDeUsuario build() {
        ContatoDeUsuario contato = new ContatoDeUsuario();
        
        // Campos herdados
        contato.setValor(getValor());
        contato.setNome(getNome());
        contato.setTipo(getTipo());
        // Campo exclusivo
        contato.setEVisivel(eVisivel);
        
        return contato;
    }
}