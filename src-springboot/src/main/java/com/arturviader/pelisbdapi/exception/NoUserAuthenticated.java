package com.arturviader.pelisbdapi.exception;

public class NoUserAuthenticated extends RuntimeException{
    public NoUserAuthenticated(){
        super("No hay usuario autentificado.");
    }
}
