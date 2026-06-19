package com.arturviader.pelisbdapi.exception;

public class IncorrectTimeWidow extends RuntimeException {
    public IncorrectTimeWidow() {
        super("Timewidow puede solo ser Day o Week.");
    }
}
