package com.arturviader.pelisbdapi.dto;

public record JwtResponse(
        String token,
        String type,
        Long id,
        String email,
        String userName,
        String role
) {
    public JwtResponse(String token, Long id, String email, String userName, String role) {
        this(token, "Bearer", id, email, userName, role);
    }
}
