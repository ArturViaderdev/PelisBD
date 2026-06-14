package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WatchedRequest(
        String type,
        Long itemId
) {}
