package com.arturviader.pelisbdapi.dto;

public record MarkEpisodeRequest(
        Integer season,
        Integer episode,
        Boolean watched
) {
}
