package com.arturviader.pelisbdapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenreListResponseTMDB {
    private List<Genre> genres;

    public GenreListResponseTMDB(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
