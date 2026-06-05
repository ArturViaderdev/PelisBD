package com.arturviader.pelisbdapi.dto;

public record UserResponse (
        Long id,
        String email,
        String userName,
        String role
){
}
