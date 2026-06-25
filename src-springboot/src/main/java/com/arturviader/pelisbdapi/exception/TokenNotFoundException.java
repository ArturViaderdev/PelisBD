package com.arturviader.pelisbdapi.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("Token no encontrado.");
    }
}
