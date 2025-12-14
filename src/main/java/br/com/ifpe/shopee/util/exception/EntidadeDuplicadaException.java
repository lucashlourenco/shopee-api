// src/main/java/br.com.ifpe.shopee.util/exception/EntidadeDuplicadaException.java

package br.com.ifpe.shopee.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409
public class EntidadeDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntidadeDuplicadaException(String msg) {
        super(msg);
    }
}