package com.arturviader.pelisbdapi.model;

import lombok.Data;

@Data
public class SerieTMDB {
    private Long id;
    private String name;
    private String overview;
    private String firstAirDate;
    private Double voteAverage;
    private String posterPath;

    public SerieTMDB(Long id, String name, String overview, String firstAirDate, Double voteAverage, String posterPath, String backdropPath) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.firstAirDate = firstAirDate;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    private String backdropPath;
}
