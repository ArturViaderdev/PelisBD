package com.arturviader.pelisbdapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SerieTMDB {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("first_air_date")
    private String firstAirDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("popularity")
    private Double popularity;

    public SerieTMDB(Long id, String name, String originalName, String overview, String posterPath, String firstAirDate, Double voteAverage, Integer voteCount, Double popularity) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.overview = overview;
        this.posterPath = posterPath;
        this.firstAirDate = firstAirDate;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.popularity = popularity;
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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
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

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
}
