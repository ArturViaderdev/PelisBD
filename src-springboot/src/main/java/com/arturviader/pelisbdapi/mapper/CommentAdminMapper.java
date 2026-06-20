package com.arturviader.pelisbdapi.mapper;

import com.arturviader.pelisbdapi.dto.CommentAdminDto;
import com.arturviader.pelisbdapi.model.Comment;

public class CommentAdminMapper {
    public static CommentAdminDto toDto(Comment comment) {
        return new CommentAdminDto(
                comment.getId(),
                comment.getUser() != null ? comment.getUser().getUserName() : null,
                comment.getItemId(),
                comment.getItemType(),
                comment.getCommentText(),
                comment.isPublic(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
