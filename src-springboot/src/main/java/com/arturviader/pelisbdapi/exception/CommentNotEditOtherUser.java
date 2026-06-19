package com.arturviader.pelisbdapi.exception;

public class CommentNotEditOtherUser extends RuntimeException{
    public CommentNotEditOtherUser(){
        super("No puedes editar comentarios de otros usuarios.");
    }
}
