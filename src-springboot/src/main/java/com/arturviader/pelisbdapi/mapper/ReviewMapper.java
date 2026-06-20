package com.arturviader.pelisbdapi.mapper;

import com.arturviader.pelisbdapi.dto.ReviewDto;
import com.arturviader.pelisbdapi.model.Review;

public class ReviewMapper {
    public static ReviewDto toDto(Review review){
        return new ReviewDto(review.getId(),review.getUserId(),review.getTmdbId(),review.getMediaType().toString(),review.getRating(),review.getCreatedAt());
    }
}
