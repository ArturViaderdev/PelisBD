package com.arturviader.pelisbdapi;



import com.arturviader.pelisbdapi.dto.CommentDto;
import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import com.arturviader.pelisbdapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CommentControllerTests {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String TEST_API_KEY = "TEST-API-KEY";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User normalUser;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(get("/").header(API_KEY_HEADER, TEST_API_KEY))
                .build();

        commentRepository.deleteAll();
        userRepository.deleteAll();

        User adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setUserName("admin");
        adminUser.setPassword("secret");
        adminUser.setEnabled(true);
        adminUser.setRole(Role.USER);
        userRepository.save(adminUser);

        normalUser = new User();
        normalUser.setEmail("artur2@test.com");
        normalUser.setUserName("artur2");
        normalUser.setPassword("secret");
        normalUser.setEnabled(true);
        normalUser.setRole(Role.USER);
        userRepository.save(normalUser);

        User persistedUser = userRepository.findByUserName("artur2").orElseThrow();

        Comment comment = new Comment();
        comment.setUser(persistedUser);
        comment.setItemId(123L);
        comment.setItemType(MediaType.movie);
        comment.setCommentText("Comentario de prueba");
        comment.setPublic(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        savedComment = commentRepository.save(comment);
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void addComment_shouldCreateComment() throws Exception {
        mockMvc.perform(post("/api/reviews/movie/123/comments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"text":"Nuevo comentario","isPublic":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Nuevo comentario"))
                .andExpect(jsonPath("$.userName").value("artur2"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getComments_shouldReturnComments() throws Exception {
        mockMvc.perform(get("/api/reviews/movie/123/comments")
                        .param("onlyPublic", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedComment.getId()))
                .andExpect(jsonPath("$[0].userName").value("artur2"))
                .andExpect(jsonPath("$[0].text").value("Comentario de prueba"))
                .andExpect(jsonPath("$[0].public").value(true));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void deleteComment_shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/reviews/comments/{commentId}", savedComment.getId()))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(savedComment.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void updateComment_shouldUpdateComment() throws Exception {
        mockMvc.perform(put("/api/reviews/comments/{commentId}", savedComment.getId())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"text":"Comentario actualizado"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Comentario actualizado"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void addComment_shouldReturnBadRequest_whenTextIsEmpty() throws Exception {
        mockMvc.perform(post("/api/reviews/movie/123/comments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"text":" ","isPublic":true}
                                """))
                .andExpect(status().isBadRequest());
    }
}