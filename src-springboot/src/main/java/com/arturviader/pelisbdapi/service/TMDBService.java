package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MoviesResponseTMDB;
import com.arturviader.pelisbdapi.model.MovieDetailTMDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TMDBService {
    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public MoviesResponseTMDB searchMovie(String query) {
        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&api_key=" + apiKey;

        ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
        return response.getBody();
    }

    public MovieDetailTMDB getMovieById(Long id) {
        String url = "https://api.themoviedb.org/3/movie/" + id +
                "?api_key=" + apiKey + "&language=es-ES";

        ResponseEntity<MovieDetailTMDB> response = restTemplate.getForEntity(url, MovieDetailTMDB.class);
        return response.getBody();
    }
}
