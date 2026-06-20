package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MovieTMDB;
import com.arturviader.pelisbdapi.model.Movie;

import java.util.Optional;

public interface MovieService {
    Movie saveIfNotExists(MovieTMDB tmdbMovie, String mediaType);

    Optional<Movie> findById(Long tmdbId);
}
