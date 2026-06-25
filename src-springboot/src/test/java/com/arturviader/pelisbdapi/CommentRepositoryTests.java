package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private Comment moviePublicComment;
    private Comment moviePrivateComment;
    private Comment tvPublicComment;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("juan@mail.es");
        user1.setUserName("Juan");
        user1.setPassword("encodedPassword");
        user1.setEnabled(true);
        user1.setRole(Role.USER);

        user2 = new User();
        user2.setEmail("ana@mail.es");
        user2.setUserName("Ana");
        user2.setPassword("encodedPassword");
        user2.setEnabled(true);
        user2.setRole(Role.USER);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        moviePublicComment = new Comment();
        moviePublicComment.setUser(user1);
        moviePublicComment.setItemId(10L);
        moviePublicComment.setItemType(MediaType.movie);
        moviePublicComment.setCommentText("Comentario público de movie");
        moviePublicComment.setPublic(true);
        moviePublicComment.setCreatedAt(LocalDateTime.now());
        moviePublicComment.setUpdatedAt(LocalDateTime.now());

        moviePrivateComment = new Comment();
        moviePrivateComment.setUser(user1);
        moviePrivateComment.setItemId(10L);
        moviePrivateComment.setItemType(MediaType.movie);
        moviePrivateComment.setCommentText("Comentario privado de movie");
        moviePrivateComment.setPublic(false);
        moviePrivateComment.setCreatedAt(LocalDateTime.now());
        moviePrivateComment.setUpdatedAt(LocalDateTime.now());

        tvPublicComment = new Comment();
        tvPublicComment.setUser(user2);
        tvPublicComment.setItemId(20L);
        tvPublicComment.setItemType(MediaType.tv);
        tvPublicComment.setCommentText("Comentario público de tv");
        tvPublicComment.setPublic(true);
        tvPublicComment.setCreatedAt(LocalDateTime.now());
        tvPublicComment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findByItemIdAndItemTypeAndIsPublicTrue_shouldReturnOnlyPublicComments() {
        entityManager.persist(moviePublicComment);
        entityManager.persist(moviePrivateComment);
        entityManager.persist(tvPublicComment);
        entityManager.flush();

        List<Comment> result = commentRepository.findByItemIdAndItemTypeAndIsPublicTrue(10L, MediaType.movie);

        assertEquals(1, result.size());
        assertEquals("Comentario público de movie", result.get(0).getCommentText());
    }

    @Test
    void findByItemIdAndItemType_shouldReturnAllCommentsForItemAndType() {
        entityManager.persist(moviePublicComment);
        entityManager.persist(moviePrivateComment);
        entityManager.persist(tvPublicComment);
        entityManager.flush();

        List<Comment> result = commentRepository.findByItemIdAndItemType(10L, MediaType.movie);

        assertEquals(2, result.size());
    }

    @Test
    void findByUserIdAndItemIdAndItemType_shouldReturnOnlyThatUsersComments() {
        entityManager.persist(moviePublicComment);
        entityManager.persist(moviePrivateComment);
        entityManager.persist(tvPublicComment);
        entityManager.flush();

        List<Comment> result = commentRepository.findByUserIdAndItemIdAndItemType(
                user1.getId(), 10L, MediaType.movie
        );

        assertEquals(2, result.size());
    }

    @Test
    void findByItemType_shouldReturnPagedComments() {
        entityManager.persist(moviePublicComment);
        entityManager.persist(moviePrivateComment);
        entityManager.persist(tvPublicComment);
        entityManager.flush();

        Page<Comment> result = commentRepository.findByItemType(MediaType.movie, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }
}