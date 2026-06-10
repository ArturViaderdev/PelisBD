package com.arturviader.pelisbdapi.model;

import lombok.Data;

import java.util.List;

@Data
public class GenreDetailMoviesTMDB {
    private Long id;
    private String name;
    private List<MovieTMDB> movies;

    public GenreDetailMoviesTMDB(Long id, String name, List<MovieTMDB> movies) {
        this.id = id;
        this.name = name;
        this.movies = movies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MovieTMDB> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieTMDB> movies) {
        this.movies = movies;
    }
}
