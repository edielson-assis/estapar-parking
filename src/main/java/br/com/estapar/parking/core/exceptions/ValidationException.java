package br.com.estapar.parking.core.exceptions;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
}