package com.arturviader.pelisbdapi.exception;

public class EmailNotConfirmed extends RuntimeException {
    public EmailNotConfirmed() {
        super("Email no confirmado.");
    }
}
