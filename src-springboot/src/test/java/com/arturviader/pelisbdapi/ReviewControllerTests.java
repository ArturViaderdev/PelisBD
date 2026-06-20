package com.arturviader.pelisbdapi;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.CommentRepository;
import com.arturviader.pelisbdapi.repository.ReviewRepository;
import com.arturviader.pelisbdapi.repository.UserRepository;
import com.arturviader.pelisbdapi.service.ReviewService;
import com.arturviader.pelisbdapi.service.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReviewControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("artur2@test.com");
        user.setUserName("artur2");
        user.setPassword("secret");
        user.setEnabled(true);
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Test
    void rateItem_shouldCreateReviewAndReturnRatings() throws Exception {
        mockMvc.perform(post("/api/reviews/movie/123/rate")
                        .with(user("artur2").roles("USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"rating":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(5.0))
                .andExpect(jsonPath("$.totalRatings").value(1))
                .andExpect(jsonPath("$.hasUserRated").value(true))
                .andExpect(jsonPath("$.userRating").value(5));
    }

    @Test
    void getItemRatings_shouldReturnEmptyRatingsWhenNoReviewExists() throws Exception {
        mockMvc.perform(get("/api/reviews/movie/123/ratings")
                        .with(user("artur2").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(0.0))
                .andExpect(jsonPath("$.totalRatings").value(0))
                .andExpect(jsonPath("$.hasUserRated").value(false))
                .andExpect(jsonPath("$.userRating").isEmpty());
    }

    @Test
    void rateItem_shouldReturnBadRequest_whenRatingIsInvalid() throws Exception {
        mockMvc.perform(post("/api/reviews/movie/123/rate")
                        .with(user("artur2").roles("USER"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"rating":6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La puntuación debe ser del 1 al 5."));
    }
}