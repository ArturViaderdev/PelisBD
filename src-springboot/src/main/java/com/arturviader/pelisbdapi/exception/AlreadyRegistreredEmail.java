package com.arturviader.pelisbdapi.exception;

public class AlreadyRegistreredEmail extends RuntimeException {
    public AlreadyRegistreredEmail() {
        super("Email ya registrado.");
    }
}
