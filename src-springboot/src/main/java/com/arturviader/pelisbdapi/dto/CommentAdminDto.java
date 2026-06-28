package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MediaType;

import java.time.LocalDateTime;

public record CommentAdminDto(
        Long id,
        String userName,
        Long itemId,
        MediaType itemType,
        String commentText,
        boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}