package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TvRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTmdbId(Long tmdbId);
}
