package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.exception.*;
import com.arturviader.pelisbdapi.mapper.CommentMapper;
import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Override
    public CommentDto addComment(Long itemId, MediaType type, String text, boolean isPublic) {
        User user = userService.getCurrentUser();
        if (text == null || text.trim().isEmpty()) {
            throw new CommentEmptyException();
        }
        if (text.length() > 1000) {
            throw new CommentLargeException();
        }
        if (!isPublic) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
            List<Comment> recentComments = commentRepository.findByUserIdAndItemIdAndItemType(
                    user.getId(), itemId, type
            );
            if (recentComments.stream().anyMatch(c -> c.getCreatedAt().isAfter(fiveMinutesAgo))) {
                throw new CommentTimeLimitException();
            }
        }
        Comment comment = new Comment(user, itemId, type, text.trim(), isPublic, LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved, user);
    }

    @Override
    public List<CommentDto> getComments(Long itemId, MediaType type, boolean onlyPublic) {
        User user = userService.getCurrentUser();
        List<Comment> comments = onlyPublic
                ? commentRepository.findByItemIdAndItemTypeAndIsPublicTrue(itemId, type)
                : commentRepository.findByItemIdAndItemType(itemId, type);
        return comments.stream()
                .filter(c -> c.isPublic() || c.getUser().getId().equals(user.getId()))
                .map(c -> CommentMapper.toDto(c, c.getUser()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        User user = userService.getCurrentUser();
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new CommentNotDeleteOtherUser();
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateComment(Long commentId, String newText) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        User user = userService.getCurrentUser();
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new CommentNotEditOtherUser();
        }

        if (newText == null || newText.trim().isEmpty()) {
            throw new CommentEmptyException();
        }

        if (newText.length() > 1000) {
            throw new CommentLargeException();
        }
        comment.setCommentText(newText.trim());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updated = commentRepository.save(comment);
        return CommentMapper.toDto(updated, updated.getUser());
    }

    /*@Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentDto> dto = new ArrayList<>();
        for(Comment comment:comments){

        }
    }*/
}
