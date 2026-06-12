package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MovieTMDB;
import com.arturviader.pelisbdapi.model.SerieTMDB;

import java.util.List;

public class SearchResultsMoviesAndTV {
    List<MovieTMDB> movies;
    List<SerieTMDB> series;

    public SearchResultsMoviesAndTV(List<MovieTMDB> movies, List<SerieTMDB> series) {
        this.movies = movies;
        this.series = series;
    }

    public List<MovieTMDB> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieTMDB> movies) {
        this.movies = movies;
    }

    public List<SerieTMDB> getSeries() {
        return series;
    }

    public void setSeries(List<SerieTMDB> series) {
        this.series = series;
    }
}
