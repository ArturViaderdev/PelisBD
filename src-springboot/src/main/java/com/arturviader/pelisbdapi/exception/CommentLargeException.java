package com.arturviader.pelisbdapi.exception;

public class CommentLargeException extends RuntimeException {
    public CommentLargeException() {
        super("El comentario no puede tener más de 1000 caracteres.");
    }
}
