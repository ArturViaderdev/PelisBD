package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class CommentAdminController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllComments() {
        //return commentService.getAllComments();
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comentario eliminado");
    }
}

