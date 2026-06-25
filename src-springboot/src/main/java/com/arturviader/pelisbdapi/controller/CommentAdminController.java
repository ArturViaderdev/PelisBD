package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.CommentAdminDto;
import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = "*")
public class CommentAdminController {

    private final CommentService commentService;

    public CommentAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Page<CommentAdminDto>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getAllCommentsAdmin(type, search, pageable));
    }

    @DeleteMapping("/{commentId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
