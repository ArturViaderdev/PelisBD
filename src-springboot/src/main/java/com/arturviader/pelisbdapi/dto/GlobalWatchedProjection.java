package com.arturviader.pelisbdapi.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public interface GlobalWatchedProjection {
    Long getItemId();
    String getType();
    LocalDateTime getWatchedAt();
}