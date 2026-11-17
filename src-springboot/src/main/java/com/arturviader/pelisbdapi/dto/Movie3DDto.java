package com.arturviader.pelisbdapi.dto;

import java.time.LocalDateTime;

/**
 * Película 3D aprobada (listado público).
 * watched / watchListed: flags del usuario logueado (false si es anónimo).
 */
public record Movie3DDto(
        Long tmdbId,
        String title,
        String posterPath,
        String releaseDate,
        LocalDateTime approvedAt,
        boolean watched,
        boolean watchListed
) {
}
