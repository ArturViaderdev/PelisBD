package com.arturviader.pelisbdapi.dto;

import java.time.LocalDateTime;

public record EpisodeProgressDto(
        Long tvId,
        Integer seasonNumber,
        Integer episodeNumber,
        Boolean watched,
        LocalDateTime watchedAt
) {
}