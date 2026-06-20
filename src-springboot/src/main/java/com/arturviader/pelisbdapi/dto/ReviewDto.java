package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MediaType;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        String userId,
        Long tmdbId,
        String mediaType,
        Integer rating,
        LocalDateTime createdAt
) {
}
