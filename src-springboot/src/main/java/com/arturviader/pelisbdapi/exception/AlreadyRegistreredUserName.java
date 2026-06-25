package com.arturviader.pelisbdapi.exception;

public class AlreadyRegistreredUserName extends RuntimeException {
    public AlreadyRegistreredUserName() {
        super("Nombre de usuario ya registrado.");
    }
}
