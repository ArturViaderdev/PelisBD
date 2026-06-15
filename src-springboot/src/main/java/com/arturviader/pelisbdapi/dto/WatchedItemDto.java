package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record WatchedItemDto(
        @JsonProperty("id") Long id,
        @JsonProperty("type") String type,
        @JsonProperty("added_at") LocalDateTime addedAt,
        @JsonProperty("title") String title,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("release_date") String releaseDate, // ✅ yyyy-MM-dd
        @JsonProperty("media_type") String mediaType
) {
}
