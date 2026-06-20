package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.model.Review;

import java.util.Map;

public interface ReviewService {
    double getAverageRating(Long tmdbId, String mediaType);

    Long getTotalRatings(Long tmdbId, String mediaType);

    int getUserRate(String userId, Long tmdbId, String mediaType);

    Map<String, Object> getRatings(String userId, Long tmdbId, String mediaType);

    Review rateItem(String userId, Long tmdbId, String mediaType, Integer rating);
}
