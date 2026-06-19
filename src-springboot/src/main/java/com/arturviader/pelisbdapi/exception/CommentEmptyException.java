package com.arturviader.pelisbdapi.exception;

public class CommentEmptyException extends RuntimeException {
    public CommentEmptyException() {
        super("El comentario no puede estar vacío.");
    }
}
