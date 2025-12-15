// src/main/java/br/com/ifpe/shopee.util/exception/AdvertenciaException.java

package br.com.ifpe.shopee.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// HTTP 422 - Unprocessable Entity (Entidade Não Processável) 
// A requisição está bem-formada, mas foi incapaz de ser seguida devido a erros semânticos.
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AdvertenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AdvertenciaException(String msg) {
        super(msg);
    }
}