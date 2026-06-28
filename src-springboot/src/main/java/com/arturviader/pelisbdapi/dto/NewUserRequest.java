package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewUserRequest(
        @NotBlank @Email @JsonProperty("email") String email,
        @NotBlank @JsonProperty("password") String password,
        @NotBlank @JsonProperty("userName") String userName
) {
}