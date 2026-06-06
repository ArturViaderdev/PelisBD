package com.arturviader.pelisbdapi.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int status,
        String error,
        String path,
        long timestamp
) {
    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status.value(), message, path, System.currentTimeMillis());
    }
}
