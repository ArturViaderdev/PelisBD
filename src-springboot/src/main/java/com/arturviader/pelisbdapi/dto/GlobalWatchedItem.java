package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MediaType;

import java.time.LocalDateTime;

public record GlobalWatchedItem(Long itemId, MediaType type, LocalDateTime watchedAt) {
}