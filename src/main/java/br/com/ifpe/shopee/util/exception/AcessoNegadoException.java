// src/main/java/br.com.ifpe.shopee.util.exception/AcessoNegadoException.java

package br.com.ifpe.shopee.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// HTTP 403 - Forbidden (Acesso Negado)
// A requisição foi entendida e aceita, mas foi negada devido a um problema de segurança.
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String msg) {
        super(msg);
    }
}