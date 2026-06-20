package com.arturviader.pelisbdapi.dto;

import org.springframework.data.annotation.Id;

public record UserResponse(
        Long id,
        String email,
        String userName,
        String role
) {
}
