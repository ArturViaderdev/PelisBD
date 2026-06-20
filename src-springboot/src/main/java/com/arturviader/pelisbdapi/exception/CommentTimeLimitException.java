package com.arturviader.pelisbdapi.exception;

public class CommentTimeLimitException extends RuntimeException {
    public CommentTimeLimitException() {
        super("No puedes enviar más de un comentario privado en menos de 5 minutos");
    }
}
