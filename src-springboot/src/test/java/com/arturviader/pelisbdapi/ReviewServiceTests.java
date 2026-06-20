package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Review;
import com.arturviader.pelisbdapi.repository.ReviewRepository;
import com.arturviader.pelisbdapi.service.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void getAverageRating_shouldReturnAverage_whenRepositoryReturnsValue() {
        when(reviewRepository.findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(4.5);

        double result = reviewService.getAverageRating(123L, "movie");

        assertThat(result).isEqualTo(4.5);
        verify(reviewRepository).findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie);
    }

    @Test
    void getAverageRating_shouldReturnZero_whenRepositoryReturnsNull() {
        when(reviewRepository.findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(null);

        double result = reviewService.getAverageRating(123L, "movie");

        assertThat(result).isEqualTo(0.0);
        verify(reviewRepository).findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie);
    }

    @Test
    void getTotalRatings_shouldReturnTotal() {
        when(reviewRepository.countTotalRatingsByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(7L);

        Long result = reviewService.getTotalRatings(123L, "movie");

        assertThat(result).isEqualTo(7L);
        verify(reviewRepository).countTotalRatingsByTmdbIdAndMediaType(123L, MediaType.movie);
    }

    @Test
    void getUserRate_shouldReturnUserRating_whenReviewExists() {
        Review review = new Review();
        review.setRating(4);

        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.of(review));

        int result = reviewService.getUserRate("artur2", 123L, "movie");

        assertThat(result).isEqualTo(4);
        verify(reviewRepository, times(2))
                .findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie);
    }

    @Test
    void getUserRate_shouldReturnZero_whenReviewDoesNotExist() {
        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.empty());

        int result = reviewService.getUserRate("artur2", 123L, "movie");

        assertThat(result).isEqualTo(0);
        verify(reviewRepository, times(1))
                .findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie);
    }

    @Test
    void getRatings_shouldReturnFullMap_whenUserHasRated() {
        Review review = new Review();
        review.setRating(5);

        when(reviewRepository.findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(4.8);
        when(reviewRepository.countTotalRatingsByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(10L);
        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.of(review));

        Map<String, Object> result = reviewService.getRatings("artur2", 123L, "movie");

        assertThat(result.get("averageRating")).isEqualTo(4.8);
        assertThat(result.get("totalRatings")).isEqualTo(10L);
        assertThat(result.get("hasUserRated")).isEqualTo(true);
        assertThat(result.get("userRating")).isEqualTo(5);
    }

    @Test
    void getRatings_shouldReturnUserRatingNull_whenUserHasNotRated() {
        when(reviewRepository.findAverageRatingByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(4.0);
        when(reviewRepository.countTotalRatingsByTmdbIdAndMediaType(123L, MediaType.movie))
                .thenReturn(3L);
        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.empty());

        Map<String, Object> result = reviewService.getRatings("artur2", 123L, "movie");

        assertThat(result.get("averageRating")).isEqualTo(4.0);
        assertThat(result.get("totalRatings")).isEqualTo(3L);
        assertThat(result.get("hasUserRated")).isEqualTo(false);
        assertThat(result.get("userRating")).isNull();
    }

    @Test
    void rateItem_shouldSaveNewReview() {
        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.rateItem("artur2", 123L, "movie", 5);

        assertThat(result.getUserId()).isEqualTo("artur2");
        assertThat(result.getTmdbId()).isEqualTo(123L);
        assertThat(result.getMediaType()).isEqualTo(MediaType.movie);
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void rateItem_shouldUpdateExistingReview() {
        Review existing = new Review();
        existing.setUserId("artur2");
        existing.setTmdbId(123L);
        existing.setMediaType(MediaType.movie);
        existing.setRating(3);
        existing.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(reviewRepository.findByUserIdAndTmdbIdAndMediaType("artur2", 123L, MediaType.movie))
                .thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.rateItem("artur2", 123L, "movie", 5);

        assertThat(result.getUserId()).isEqualTo("artur2");
        assertThat(result.getTmdbId()).isEqualTo(123L);
        assertThat(result.getMediaType()).isEqualTo(MediaType.movie);
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }
}