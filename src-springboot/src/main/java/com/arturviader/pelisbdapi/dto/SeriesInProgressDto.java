package com.arturviader.pelisbdapi.dto;

import java.time.LocalDateTime;

/**
 * Serie con al menos un capítulo visto por el usuario.
 * lastSeason / lastEpisode = capítulo más avanzado visto (mayor temporada y, dentro de ella, mayor capítulo).
 */
public record SeriesInProgressDto(
        Long tvId,
        String name,
        String posterPath,
        Integer lastSeason,
        Integer lastEpisode,
        Long watchedEpisodes,
        LocalDateTime lastWatchedAt
) {
}
