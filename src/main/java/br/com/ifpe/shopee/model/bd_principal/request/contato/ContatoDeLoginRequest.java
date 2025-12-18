// src/main/java/br.com.ifpe.shopee.model/bd_principal/request/contato/ContatoDeLoginRequest.java

package br.com.ifpe.shopee.model.bd_principal.request.contato;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.request.contato.abstrato.ContatoRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ContatoDeLoginRequest extends ContatoRequest {

    /**
     * Converte o Request (DTO) para a Entidade de Persistência,
     * usando setters para popular campos herdados.
     */
    public ContatoDeLogin build() {
        ContatoDeLogin contato = new ContatoDeLogin();
        
        contato.setValor(getValor());
        
        // A associação com Usuario é feita na camada Service (UsuarioService ou ContatoDeLoginService)
        
        return contato;
    }
}


