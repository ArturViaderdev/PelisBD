package com.arturviader.pelisbdapi.dto;

import java.time.LocalDateTime;

/** Película propuesta como 3D pendiente de revisión, con cuántos usuarios la proponen. */
public record Movie3DPendingDto(
        Long tmdbId,
        String title,
        String posterPath,
        String releaseDate,
        long requestCount,
        LocalDateTime firstRequestedAt
) {
}
