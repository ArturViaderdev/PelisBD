package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.dto.CommentAdminDto;
import com.arturviader.pelisbdapi.mapper.CommentAdminMapper;
import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import com.arturviader.pelisbdapi.exception.CommentNotFoundException;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTests {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User admin;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setUserName("admin");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);
        user = new User();
        user.setId(2L);
        user.setUserName("artur2");
        user.setEmail("artur2@test.com");
        user.setRole(Role.USER);
        comment = new Comment();
        comment.setId(10L);
        comment.setUser(user);
        comment.setItemId(123L);
        comment.setItemType(MediaType.movie);
        comment.setCommentText("Comentario de prueba");
        comment.setPublic(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllCommentsAdmin_withoutFilters_returnsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findAll(pageable)).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin(null, null, pageable);
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(10L);
        assertThat(result.getContent().get(0).userName()).isEqualTo("artur2");
        assertThat(result.getContent().get(0).itemId()).isEqualTo(123L);
        assertThat(result.getContent().get(0).itemType()).isEqualTo(MediaType.movie);
        assertThat(result.getContent().get(0).commentText()).isEqualTo("Comentario de prueba");
        verify(commentRepository).findAll(pageable);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getAllCommentsAdmin_withTypeAndSearch_filtersCorrectly() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findByItemTypeAndCommentTextContainingIgnoreCase(
                MediaType.movie, "prueba", pageable
        )).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin("movie", "prueba", pageable);
        assertThat(result).hasSize(1);
        verify(commentRepository).findByItemTypeAndCommentTextContainingIgnoreCase(
                MediaType.movie, "prueba", pageable
        );
    }

    @Test
    void getAllCommentsAdmin_withOnlyType_filtersCorrectly() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findByItemType(MediaType.movie, pageable)).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin("movie", null, pageable);
        assertThat(result).hasSize(1);
        verify(commentRepository).findByItemType(MediaType.movie, pageable);
    }

    @Test
    void getAllCommentsAdmin_withOnlySearch_filtersCorrectly() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findByCommentTextContainingIgnoreCase("prueba", pageable)).thenReturn(page);
        Page<CommentAdminDto> result = commentService.getAllCommentsAdmin(null, "prueba", pageable);
        assertThat(result).hasSize(1);
        verify(commentRepository).findByCommentTextContainingIgnoreCase("prueba", pageable);
    }

    @Test
    void deleteComment_deletesWhenExists() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(admin);
        commentService.deleteComment(10L);
        verify(commentRepository).deleteById(10L);
    }

    @Test
    void deleteComment_throwsWhenCommentNotFound() {
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.deleteComment(10L))
                .isInstanceOf(CommentNotFoundException.class);
        verify(commentRepository, never()).deleteById(anyLong());
    }
}