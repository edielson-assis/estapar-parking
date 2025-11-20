package br.com.estapar.parking.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    
    public ObjectNotFoundException(String message) {
        super(message);
    }
}