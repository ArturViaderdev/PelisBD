package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.model.Comment;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import com.arturviader.pelisbdapi.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentAdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Long commentId;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUserName("artur2");
        user.setEmail("artur2@test.com");
        user.setPassword("secret");
        user.setRole(Role.USER);
        userRepository.save(user);

        User userAdmin = new User();
        userAdmin.setUserName("artur");
        userAdmin.setEmail("artur@test.com");
        userAdmin.setPassword("secret");
        userAdmin.setRole(Role.ADMIN);
        userRepository.save(userAdmin);

        Comment comment = new Comment();
        comment.setUser(userAdmin); // <-- si quieres que el comentario sea de artur
        comment.setItemId(123L);
        comment.setItemType(MediaType.movie);
        comment.setCommentText("Comentario de prueba");
        comment.setPublic(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        commentId = comment.getId();
    }

    @Test
    @WithMockUser(username = "artur", roles = "ADMIN")
    void getAllComments_returnsCommentsFromDb() throws Exception {
        mockMvc.perform(get("/api/admin/comments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(commentId))
                .andExpect(jsonPath("$.content[0].userName").value("artur"))
                .andExpect(jsonPath("$.content[0].itemId").value(123))
                .andExpect(jsonPath("$.content[0].itemType").value("movie"))
                .andExpect(jsonPath("$.content[0].commentText").value("Comentario de prueba"))
                .andExpect(jsonPath("$.content[0].isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "artur", roles = "ADMIN")
    void deleteComment_removesCommentFromDatabase() throws Exception {
        mockMvc.perform(delete("/api/admin/comments/{commentId}", commentId))
                .andExpect(status().isNoContent());

        Assertions.assertThat(commentRepository.findById(commentId)).isEmpty();
    }
}