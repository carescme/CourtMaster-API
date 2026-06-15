package com.courtmaster.api.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}