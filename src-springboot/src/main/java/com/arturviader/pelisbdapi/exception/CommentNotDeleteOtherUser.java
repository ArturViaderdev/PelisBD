package com.arturviader.pelisbdapi.exception;

public class CommentNotDeleteOtherUser extends RuntimeException{
    public CommentNotDeleteOtherUser(){
        super("No puedes eliminar comentarios de otros usuarios.");
    }
}
