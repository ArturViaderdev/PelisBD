package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndTmdbIdAndMediaType(String userId, Long tmdbId, String mediaType);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tmdbId = :tmdbId AND r.mediaType = :mediaType")
    Double findAverageRatingByTmdbIdAndMediaType(@Param("tmdbId") Long tmdbId, @Param("mediaType") String mediaType);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.tmdbId = :tmdbId AND r.mediaType = :mediaType")
    Long countTotalRatingsByTmdbIdAndMediaType(@Param("tmdbId") Long tmdbId, @Param("mediaType") String mediaType);
}

