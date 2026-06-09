package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MovieResponseTMDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TMDBService {
    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public MovieResponseTMDB searchMovie(String query) {
        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&api_key=" + apiKey;

        ResponseEntity<MovieResponseTMDB> response = restTemplate.getForEntity(url, MovieResponseTMDB.class);
        return response.getBody();
    }
}
