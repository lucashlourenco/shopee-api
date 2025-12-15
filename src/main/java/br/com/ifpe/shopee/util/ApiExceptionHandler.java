// src/main/java/br.com.ifpe.shopee.api/handler/ApiExceptionHandler.java

package br.com.ifpe.shopee.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

import org.springframework.http.HttpStatus;


@ControllerAdvice
public class ApiExceptionHandler {

    // Trata exceções do tipo 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<?> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        // Usa o status 404 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Trata exceções do tipo 409
    @ExceptionHandler(EntidadeDuplicadaException.class)
    public ResponseEntity<?> handleEntidadeDuplicada(EntidadeDuplicadaException ex) {
        // Usa o status 409 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Trata exceções do tipo 422
    @ExceptionHandler(AdvertenciaException.class)
    public ResponseEntity<?> handleAdvertencia(AdvertenciaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
}