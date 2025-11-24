package br.com.estapar.parking.core.handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.estapar.parking.core.exceptions.ObjectNotFoundException;
import br.com.estapar.parking.core.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> Validation(ValidationException exception, HttpServletRequest request) {
        String error = "Validation error";
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(ObjectNotFoundException exception, HttpServletRequest request) {
        String error = "Not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> badRequest(MethodArgumentNotValidException exception, HttpServletRequest request) {
        var error = "Validation error";
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
        String error = "Bad request";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> databaseError(Exception exception, HttpServletRequest request) {
        String error = "Internal server error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(errors(status, error, exception, request));
    }
    
    private ExceptionResponse errors(HttpStatus status, String error, Exception exception, HttpServletRequest request) {
        return new ExceptionResponse(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
    }

    private Map<String, Object> errors(HttpStatus status, String typeError, MethodArgumentNotValidException exception, HttpServletRequest request) {
        var messages = exception.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList();
        var response = new HashMap<String, Object>();
        response.put("timestamp", Instant.now());
        response.put("status", status.value());
        response.put("error", typeError);
        response.put("messages", messages);
        response.put("path", request.getRequestURI());
        return response;
    }
}