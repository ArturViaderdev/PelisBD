package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Review;
import com.arturviader.pelisbdapi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public double getAverageRating(Long tmdbId, String mediaType) {
        Double average = reviewRepository.findAverageRatingByTmdbIdAndMediaType(tmdbId, mediaType);
        if (average == null) {
            return 0;
        } else {
            return average;
        }
    }

    @Override
    public Long getTotalRatings(Long tmdbId, String mediaType) {
        Long total = reviewRepository.countTotalRatingsByTmdbIdAndMediaType(tmdbId, mediaType);
        return total;
    }

    @Override
    public int getUserRate(String userId, Long tmdbId, String mediaType) {
        boolean hasUserRated = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).isPresent();
        int userRating = 0;
        if (hasUserRated) {
            userRating = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).get().getRating();
        }
        return userRating;
    }

    @Override
    public Map<String, Object> getRatings(String userId, Long tmdbId, String mediaType) {
        Double average = getAverageRating(tmdbId, mediaType);
        Long total = getTotalRatings(tmdbId, mediaType);

        boolean hasUserRated = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).isPresent();

        return Map.of(
                "averageRating", average != null ? average : 0.0,
                "totalRatings", total != null ? total : 0,
                "hasUserRated", hasUserRated,
                "userRating", hasUserRated ? reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).get().getRating() : null
        );
    }

    @Override
    public Review rateItem(String userId, Long tmdbId, String mediaType, Integer rating) {
        Optional<Review> existing = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType);
        Review review = existing.orElse(new Review());
        review.setUserId(userId);
        review.setTmdbId(tmdbId);
        review.setMediaType(MediaType.valueOf(mediaType));
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }
}

