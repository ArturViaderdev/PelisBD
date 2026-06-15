package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SerieDetailTMDB extends SerieTMDB{
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("number_of_seasons")
    private Integer numberOfSeasons;

    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("seasons")
    private List<SeasonSerieTMDB> seasons;

    private String trailerKey;

    private List<VideoTMDB> videos;

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public SerieDetailTMDB(Long id, String name, String originalName, String overview, String posterPath, String firstAirDate, Double voteAverage, Integer voteCount, Double popularity, String backdropPath, Integer numberOfSeasons, Integer numberOfEpisodes, String status, List<SeasonSerieTMDB> seasons) {
        super(id, name, originalName, overview, posterPath, firstAirDate, voteAverage, voteCount, popularity);
        this.backdropPath = backdropPath;
        this.numberOfSeasons = numberOfSeasons;
        this.numberOfEpisodes = numberOfEpisodes;
        this.status = status;
        this.seasons = seasons;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SeasonSerieTMDB> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SeasonSerieTMDB> seasons) {
        this.seasons = seasons;
    }

    public List<VideoTMDB> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoTMDB> videos) {
        this.videos = videos;
    }
}
