package com.arturviader.pelisbdapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EpisodeSerieTMDB {
    @JsonProperty("episode_number")
    private Integer episodeNumber;
    private String name;
    private String overview;
    @JsonProperty("air_date")
    private String airDate;


    public EpisodeSerieTMDB(Integer episodeNumber, String name, String overview, String airDate) {
        this.episodeNumber = episodeNumber;
        this.name = name;
        this.overview = overview;
        this.airDate = airDate;

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


}
