package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record WatchedItemDto(
        @JsonProperty("id") Long id,
        @JsonProperty("type") String type,
        @JsonProperty("itemId") Long itemId,
        @JsonProperty("added_at") LocalDateTime addedAt
) {}