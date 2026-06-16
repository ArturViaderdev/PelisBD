package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MovieTMDB;
import com.arturviader.pelisbdapi.model.Movie;
import com.arturviader.pelisbdapi.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Movie saveIfNotExists(MovieTMDB tmdbMovie, String mediaType) {
        return movieRepository.findByTmdbId(tmdbMovie.getId())
                .map(movie -> {
                    // Si ya existe, actualiza solo si cambió
                    if (needsUpdate(movie, tmdbMovie)) {
                        updateMovie(movie, tmdbMovie);
                        return movieRepository.save(movie);
                    }
                    return movie;
                })
                .orElseGet(() -> {
                    Movie movie = new Movie();
                    movie.setTmdbId(tmdbMovie.getId());
                    movie.setTitle(tmdbMovie.getTitle());
                    movie.setOverview(tmdbMovie.getOverview());
                    movie.setPosterPath(tmdbMovie.getPoster_path());
                    movie.setReleaseDate(LocalDate.parse(tmdbMovie.getRelease_date(), DateTimeFormatter.ISO_LOCAL_DATE));

                    return movieRepository.save(movie);
                });
    }

    private boolean needsUpdate(Movie movie, MovieTMDB tmdbMovie) {
        return !movie.getTitle().equals(tmdbMovie.getTitle())
                || !movie.getOverview().equals(tmdbMovie.getOverview())
                || !movie.getPosterPath().equals(tmdbMovie.getPoster_path());
    }

    private void updateMovie(Movie movie, MovieTMDB tmdbMovie) {
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setPosterPath(tmdbMovie.getPoster_path());
        movie.setReleaseDate(LocalDate.parse(tmdbMovie.getRelease_date(), DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public Optional<Movie> findById(Long tmdbId) {
        return movieRepository.findByTmdbId(tmdbId);
    }
}
