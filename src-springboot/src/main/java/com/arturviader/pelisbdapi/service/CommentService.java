package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.model.MediaType;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long itemId, MediaType type, String text, boolean isPublic);

    List<CommentDto> getComments(Long itemId, MediaType type, boolean onlyPublic);

    void deleteComment(Long commentId);

    CommentDto updateComment(Long commentId, String newText);

    //List<CommentDto> getAllComments();
}
