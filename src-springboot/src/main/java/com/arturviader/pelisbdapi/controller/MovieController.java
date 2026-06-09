package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.model.MovieDetailTMDB;
import com.arturviader.pelisbdapi.service.TMDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {
    @Autowired
    private TMDBService tmdbService;

    @GetMapping("/api/movies/search")
    public Object searchMovies(@RequestParam String query) {
        return tmdbService.searchMovie(query);
    }

    @GetMapping("/api/movies/{id}")
    public ResponseEntity<MovieDetailTMDB> getMovieById(@PathVariable Long id) {
        MovieDetailTMDB movie = tmdbService.getMovieById(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }
}
