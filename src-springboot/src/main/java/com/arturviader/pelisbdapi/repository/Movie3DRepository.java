package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.Movie3D;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Movie3DRepository extends JpaRepository<Movie3D, Long> {

    Optional<Movie3D> findByTmdbId(Long tmdbId);

    boolean existsByTmdbId(Long tmdbId);

    /** Solo los ids de TMDB: lista ligera para marcar las tarjetas como 3D en el frontend. */
    @Query("select m.tmdbId from Movie3D m")
    List<Long> findAllTmdbIds();
}
