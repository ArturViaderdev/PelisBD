package com.arturviader.pelisbdapi.model;

import lombok.Data;

@Data
public class EpisodeSerieTMDB {
    private Integer episodeNumber;
    private String name;
    private String overview;
    private String airDate;
    private Double voteAverage;

    public EpisodeSerieTMDB(Integer episodeNumber, String name, String overview, String airDate, Double voteAverage) {
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.overview = overview;
        this.airDate = airDate;
        this.voteAverage = voteAverage;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
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

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
