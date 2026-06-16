package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MovieTMDB;
import com.arturviader.pelisbdapi.dto.SerieTMDB;
import com.arturviader.pelisbdapi.model.Movie;
import com.arturviader.pelisbdapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public interface MovieService {
    Movie saveIfNotExists(MovieTMDB tmdbMovie, String mediaType);
    Optional<Movie> findById(Long tmdbId);
}
