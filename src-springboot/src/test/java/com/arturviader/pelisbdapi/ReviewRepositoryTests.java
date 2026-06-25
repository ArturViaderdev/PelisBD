package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Review;
import com.arturviader.pelisbdapi.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void clean(){
        reviewRepository.deleteAll();
    }

    @Test
    public void findByUserIdAndTmdbIdAndMediaType_shouldReturnReview() {
        Review review = new Review();
        review.setUserId("artur2");
        review.setTmdbId(123L);
        review.setMediaType(MediaType.movie);
        review.setRating(5);
        reviewRepository.save(review);
        Optional<Review> result =
                reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie);
        assertThat(result).isPresent();
        assertThat(result.get().getRating()).isEqualTo(5);
        assertThat(result.get().getUserId()).isEqualTo("artur2");
        assertThat(result.get().getTmdbId()).isEqualTo(123L);
        assertThat(result.get().getMediaType()).isEqualTo(MediaType.movie);
    }

    @Test
    public void findAverageRatingByTmdbIdAndMediaType_shouldReturnAverage() {
        Review r1 = new Review();
        r1.setUserId("u1");
        r1.setTmdbId(123L);
        r1.setMediaType(MediaType.movie);
        r1.setRating(4);
        Review r2 = new Review();
        r2.setUserId("u2");
        r2.setTmdbId(123L);
        r2.setMediaType(MediaType.movie);
        r2.setRating(2);
        reviewRepository.save(r1);
        reviewRepository.save(r2);
        Double average = reviewRepository.findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie);
        assertThat(average).isEqualTo(3.0);
    }

    @Test
    public void countTotalRatingsByTmdbIdAndMediaType_shouldReturnCount() {
        Review r1 = new Review();
        r1.setUserId("u1");
        r1.setTmdbId(123L);
        r1.setMediaType(MediaType.movie);
        r1.setRating(4);
        Review r2 = new Review();
        r2.setUserId("u2");
        r2.setTmdbId(123L);
        r2.setMediaType(MediaType.movie);
        r2.setRating(2);
        reviewRepository.save(r1);
        reviewRepository.save(r2);
        Long total = reviewRepository.countTotalRatingsByTmdbIdAndMediaType(123L, MediaType.movie);
        assertThat(total).isEqualTo(2L);
    }
}