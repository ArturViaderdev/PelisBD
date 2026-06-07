package com.arturviader.pelisbdapi.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(){
        super("Token expirado.");
    }
}
