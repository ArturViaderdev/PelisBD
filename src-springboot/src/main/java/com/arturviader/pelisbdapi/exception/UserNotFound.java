package com.arturviader.pelisbdapi.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super("Usuario no encontrado.");
    }
}
