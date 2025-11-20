package br.com.estapar.parking.core.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    
    public ObjectNotFoundException(String message) {
        super(message);
    }
}