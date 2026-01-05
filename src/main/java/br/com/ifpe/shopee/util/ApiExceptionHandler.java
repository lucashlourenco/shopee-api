// src/main/java/br.com.ifpe.shopee.api/handler/ApiExceptionHandler.java

package br.com.ifpe.shopee.util;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.com.ifpe.shopee.util.exception.AcessoNegadoException;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;


@ControllerAdvice
public class ApiExceptionHandler {

    // Trata exceções do tipo 400 - Requisição Inválida (Erros de Validação do @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Coleta todos os erros de campo (ex.: "cpf": "CPF inválido")
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Trata exceções do tipo 403 - Acesso Negado
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<?> handleAcessoNegado(AcessoNegadoException ex) {
        // Usa o status 403 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getErrorMap(ex.getMessage()));
    }

    // Trata exceções do tipo 404 - Recurso Nao Encontrado
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<?> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        // Usa o status 404 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMap(ex.getMessage()));
    }

    // Trata exceções do tipo 409 - Entidade Duplicada
    @ExceptionHandler(EntidadeDuplicadaException.class)
    public ResponseEntity<?> handleEntidadeDuplicada(EntidadeDuplicadaException ex) {
        // Usa o status 409 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorMap(ex.getMessage()));
    }

    // Trata exceções do tipo 413 - Tamanho Máximo de Arquivo Excedido
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(getErrorMap("O arquivo enviado excede o tamanho máximo permitido (${TAMANHO_MAX_ARQUIVO})."));
    }

    // Trata exceções do tipo 422 - Advertência
    @ExceptionHandler(AdvertenciaException.class)
    public ResponseEntity<?> handleAdvertencia(AdvertenciaException ex) {
        // Usa o status 422 definido no @ResponseStatus da exceção
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(getErrorMap(ex.getMessage()));
    }

    // Trata exceções do tipo 500 - Erro Interno do Servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        // Logar o erro no console é fundamental aqui
        ex.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorMap("Erro interno no servidor. Contate o suporte."));
    }

    // Método auxiliar para padronizar resposta simples de erro JSON
    private Map<String, String> getErrorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}