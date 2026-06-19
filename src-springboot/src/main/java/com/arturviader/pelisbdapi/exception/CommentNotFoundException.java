package com.arturviader.pelisbdapi.exception;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(){
        super("Comentario no encontrado.");
    }
}
