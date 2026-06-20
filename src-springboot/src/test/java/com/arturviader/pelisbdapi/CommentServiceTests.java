package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.dto.CommentAdminDto;
import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.exception.*;
import com.arturviader.pelisbdapi.mapper.CommentAdminMapper;
import com.arturviader.pelisbdapi.mapper.CommentMapper;
import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import com.arturviader.pelisbdapi.service.CommentServiceImpl;
import com.arturviader.pelisbdapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private User admin;
    private Comment comment;
    private Comment privateComment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUserName("artur2");
        user.setEmail("artur2@test.com");
        user.setRole(Role.USER);
        user.setEnabled(true);
        admin = new User();
        admin.setId(2L);
        admin.setUserName("admin");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        comment = new Comment();
        comment.setId(10L);
        comment.setUser(user);
        comment.setItemId(123L);
        comment.setItemType(MediaType.movie);
        comment.setCommentText("Comentario de prueba");
        comment.setPublic(true);
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        comment.setUpdatedAt(LocalDateTime.now());
        privateComment = new Comment();
        privateComment.setId(11L);
        privateComment.setUser(user);
        privateComment.setItemId(123L);
        privateComment.setItemType(MediaType.movie);
        privateComment.setCommentText("Comentario privado");
        privateComment.setPublic(false);
        privateComment.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        privateComment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addComment_shouldSaveAndReturnDto() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0, Comment.class);
            c.setId(99L);
            return c;
        });
        CommentDto result = commentService.addComment(123L, MediaType.movie, "  nuevo comentario  ", true);
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserName()).isEqualTo("artur2");
        assertThat(result.getText()).isEqualTo("nuevo comentario");
        assertThat(result.isPublic()).isTrue();
        verify(userService).getCurrentUser();
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowWhenTextIsEmpty() {
        when(userService.getCurrentUser()).thenReturn(user);
        assertThatThrownBy(() -> commentService.addComment(123L, MediaType.movie, "   ", true))
                .isInstanceOf(CommentEmptyException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_shouldThrowWhenTextIsTooLarge() {
        when(userService.getCurrentUser()).thenReturn(user);
        String longText = "a".repeat(1001);
        assertThatThrownBy(() -> commentService.addComment(123L, MediaType.movie, longText, true))
                .isInstanceOf(CommentLargeException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_shouldThrowWhenPrivateCommentWithinFiveMinutesExists() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findByUserIdAndItemIdAndItemType(1L, 123L, MediaType.movie))
                .thenReturn(List.of(privateComment));
        privateComment.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        assertThatThrownBy(() -> commentService.addComment(123L, MediaType.movie, "nuevo", false))
                .isInstanceOf(CommentTimeLimitException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getComments_shouldReturnPublicAndOwnCommentsForUser() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findByItemIdAndItemType(123L, MediaType.movie))
                .thenReturn(List.of(comment, privateComment));
        List<CommentDto> result = commentService.getComments(123L, MediaType.movie, false);
        assertThat(result).hasSize(2);
        verify(commentRepository).findByItemIdAndItemType(123L, MediaType.movie);
    }

    @Test
    void getComments_shouldReturnOnlyPublicForNormalUser_whenOnlyPublicTrue() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findByItemIdAndItemTypeAndIsPublicTrue(123L, MediaType.movie))
                .thenReturn(List.of(comment));
        List<CommentDto> result = commentService.getComments(123L, MediaType.movie, true);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Comentario de prueba");
    }

    @Test
    void getComments_shouldReturnAllForAdmin() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(commentRepository.findByItemIdAndItemType(123L, MediaType.movie))
                .thenReturn(List.of(comment, privateComment));
        List<CommentDto> result = commentService.getComments(123L, MediaType.movie, false);
        assertThat(result).hasSize(2);
        verify(commentRepository).findByItemIdAndItemType(123L, MediaType.movie);
    }

    @Test
    void deleteComment_shouldDeleteWhenOwner() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);
        commentService.deleteComment(10L);
        verify(commentRepository).deleteById(10L);
    }

    @Test
    void deleteComment_shouldDeleteWhenAdmin() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(admin);
        commentService.deleteComment(10L);
        verify(commentRepository).deleteById(10L);
    }

    @Test
    void deleteComment_shouldThrowWhenDifferentUserAndNotAdmin() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUserName("otro");
        otherUser.setRole(Role.USER);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(otherUser);
        assertThatThrownBy(() -> commentService.deleteComment(10L))
                .isInstanceOf(CommentNotDeleteOtherUser.class);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteComment_shouldThrowWhenNotFound() {
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.deleteComment(10L))
                .isInstanceOf(CommentNotFoundException.class);
        verify(userService, never()).getCurrentUser();
    }

    @Test
    void updateComment_shouldUpdateWhenOwner() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0, Comment.class));
        CommentDto result = commentService.updateComment(10L, "  texto actualizado  ");
        assertThat(result.getText()).isEqualTo("texto actualizado");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void updateComment_shouldThrowWhenDifferentUserAndNotAdmin() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUserName("otro");
        otherUser.setRole(Role.USER);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(otherUser);
        assertThatThrownBy(() -> commentService.updateComment(10L, "nuevo"))
                .isInstanceOf(CommentNotEditOtherUser.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldThrowWhenTextIsEmpty() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);
        assertThatThrownBy(() -> commentService.updateComment(10L, "   "))
                .isInstanceOf(CommentEmptyException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldThrowWhenTextIsTooLarge() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);
        String longText = "a".repeat(1001);
        assertThatThrownBy(() -> commentService.updateComment(10L, longText))
                .isInstanceOf(CommentLargeException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getAllCommentsAdmin_withoutFilters_returnsPage() {
        Page<Comment> page = new PageImpl<>(List.of(comment), PageRequest.of(0, 10), 1);
        when(commentRepository.findAll(any(PageRequest.class))).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin(null, null, PageRequest.of(0, 10));
        assertThat(result).hasSize(1);
        verify(commentRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getAllCommentsAdmin_withTypeAndSearch_filtersCorrectly() {
        Page<Comment> page = new PageImpl<>(List.of(comment), PageRequest.of(0, 10), 1);
        when(commentRepository.findByItemTypeAndCommentTextContainingIgnoreCase(
                eq(MediaType.movie), eq("prueba"), any(PageRequest.class)
        )).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin("movie", "prueba", PageRequest.of(0, 10));
        assertThat(result).hasSize(1);
        verify(commentRepository).findByItemTypeAndCommentTextContainingIgnoreCase(
                eq(MediaType.movie), eq("prueba"), any(PageRequest.class)
        );
    }
}