package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.service.CommentService;
import com.arturviader.pelisbdapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/{type}/{itemId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable String type,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> body
    ) {
        MediaType mediaType = MediaType.valueOf(type.toLowerCase());
        String text = (String) body.get("text");
        Boolean isPublic = (Boolean) body.get("isPublic");

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (isPublic == null) isPublic = true;

        CommentDto dto = commentService.addComment(
                itemId,
                mediaType,
                text,
                isPublic
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{type}/{itemId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable String type,
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "false") boolean onlyPublic
    ) {
        MediaType mediaType = MediaType.valueOf(type.toLowerCase());
        List<CommentDto> comments = commentService.getComments(itemId, mediaType, onlyPublic);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> body
    ) {
        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CommentDto dto = commentService.updateComment(commentId, text);
        return ResponseEntity.ok(dto);
    }
}

