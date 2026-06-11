package com.arturviader.pelisbdapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SeasonSerieTMDB {
    @JsonProperty("season_number")
    private Integer seasonNumber;
    @JsonProperty("air_date")
    private String airDate;
    @JsonProperty("name")
    private String name;
    @JsonProperty("overview")
    private String overview;

    public SeasonSerieTMDB(Integer seasonNumber, String airDate, String name, String overview) {
        this.seasonNumber = seasonNumber;
        this.airDate = airDate;
        this.name = name;
        this.overview = overview;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
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
}
