package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record WatchlistItemDto(
        @JsonProperty("id") Long id,
        @JsonProperty("type") String type,
        @JsonProperty("item_id") Long itemId,
        @JsonProperty("watched_at") LocalDateTime watchedAt
) {}
