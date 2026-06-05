package com.arturviader.pelisbdapi.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewUserRequest (
        @NotBlank @Email String email,
        @NotBlank String userName,
        @NotBlank String password
){

}
