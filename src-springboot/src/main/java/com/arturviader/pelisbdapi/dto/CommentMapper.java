package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.User;

public class CommentMapper {
    public static CommentDto toDto(Comment comment, User user) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setText(comment.getCommentText());
        dto.setPublic(comment.isPublic());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setEditable(true);
        return dto;
    }
}
