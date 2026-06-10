package com.arturviader.pelisbdapi.model;

import lombok.Data;

import java.util.List;

@Data
public class MovieDetailTMDB extends MovieTMDB {
    private Double voteAverage;
    private String backdropPath;
    private String originalLanguage;
    private List<Genre> genres;
    private List<VideoTMDB> videos;
    private String trailerKey;

    public MovieDetailTMDB(int id, String title, String release_date, String overview, String poster_path, Double voteAverage, String backdropPath, String originalLanguage, List<Genre> genres, List<VideoTMDB> videos, String trailerKey) {
        super(id, title, release_date, overview, poster_path);
        this.voteAverage = voteAverage;
        this.backdropPath = backdropPath;
        this.originalLanguage = originalLanguage;
        this.genres = genres;
        this.videos = videos;
        this.trailerKey = trailerKey;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<VideoTMDB> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoTMDB> videos) {
        this.videos = videos;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }
}
