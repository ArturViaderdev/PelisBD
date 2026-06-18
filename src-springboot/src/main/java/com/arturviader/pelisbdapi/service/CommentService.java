package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.dto.CommentMapper;
import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    public CommentDto addComment(Long itemId, MediaType type, String text, boolean isPublic) {
        User user = userService.getCurrentUser();

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }

        if (text.length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede tener más de 1000 caracteres");
        }

        if (!isPublic) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
            List<Comment> recentComments = commentRepository.findByUserIdAndItemIdAndItemType(
                    user.getId(), itemId, type
            );
            if (recentComments.stream().anyMatch(c -> c.getCreatedAt().isAfter(fiveMinutesAgo))) {
                throw new RuntimeException("No puedes enviar más de un comentario privado en menos de 5 minutos");
            }
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setItemId(itemId);
        comment.setItemType(type);
        comment.setCommentText(text.trim());
        comment.setPublic(isPublic);
        comment.setCreatedAt(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);

        return CommentMapper.toDto(saved, user);
    }

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

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        User user = userService.getCurrentUser();
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("No puedes eliminar comentarios de otros usuarios");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentDto updateComment(Long commentId, String newText) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        System.out.println("Hola");
        User user = userService.getCurrentUser();
        System.out.println("eo");
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("No puedes editar comentarios de otros usuarios");
        }

        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }

        if (newText.length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede tener más de 1000 caracteres");
        }

        comment.setCommentText(newText.trim());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentMapper.toDto(updated, updated.getUser());
    }
}
