package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.model.Review;
import com.arturviader.pelisbdapi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    //Calcular media y total de votos
    public Map<String, Object> getRatings(String userId, Long tmdbId, String mediaType) {
        Double average = reviewRepository.findAverageRatingByTmdbIdAndMediaType(tmdbId, mediaType);
        Long total = reviewRepository.countTotalRatingsByTmdbIdAndMediaType(tmdbId, mediaType);
        boolean hasUserRated = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).isPresent();

        return Map.of(
                "averageRating", average != null ? average : 0.0,
                "totalRatings", total != null ? total : 0,
                "hasUserRated", hasUserRated,
                "userRating", hasUserRated ? reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType).get().getRating() : null
        );
    }

    // Guardar o actualizar puntuación
    public Review rateItem(String userId, Long tmdbId, String mediaType, Integer rating) {
        Optional<Review> existing = reviewRepository.findByUserIdAndTmdbIdAndMediaType(userId, tmdbId, mediaType);
        Review review = existing.orElse(new Review());
        review.setUserId(userId);
        review.setTmdbId(tmdbId);
        review.setMediaType(mediaType);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }
}

