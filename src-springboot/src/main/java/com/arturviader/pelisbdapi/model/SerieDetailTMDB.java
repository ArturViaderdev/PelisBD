package com.arturviader.pelisbdapi.model;

import lombok.Data;

import java.util.List;

@Data
public class SerieDetailTMDB extends SerieTMDB{
    private String originalName;
    private String lastAirDate;
    private String status;
    private String originalLanguage;
    private Integer numberOfSeasons;
    private Integer numberOfEpisodes;
    private String backdropPath;
    private List<Genre> genres;
    private String type;
    private String homepage;
    private String inProduction;
    private List<SeasonSerieTMDB> seasons;
    private String trailerKey;
    private List<VideoTMDB> videos;

    public SerieDetailTMDB(Long id, String name, String overview, String firstAirDate, Double voteAverage, String posterPath, String backdropPath, String originalName, String lastAirDate, String status, String originalLanguage, Integer numberOfSeasons, Integer numberOfEpisodes, String backdropPath1, List<Genre> genres, String type, String homepage, String inProduction, List<SeasonSerieTMDB> seasons, String trailerKey, List<VideoTMDB> videos) {
        super(id, name, overview, firstAirDate, voteAverage, posterPath, backdropPath);
        this.originalName = originalName;
        this.lastAirDate = lastAirDate;
        this.status = status;
        this.originalLanguage = originalLanguage;
        this.numberOfSeasons = numberOfSeasons;
        this.numberOfEpisodes = numberOfEpisodes;
        this.backdropPath = backdropPath1;
        this.genres = genres;
        this.type = type;
        this.homepage = homepage;
        this.inProduction = inProduction;
        this.seasons = seasons;
        this.trailerKey = trailerKey;
        this.videos = videos;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
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

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getInProduction() {
        return inProduction;
    }

    public void setInProduction(String inProduction) {
        this.inProduction = inProduction;
    }

    public List<SeasonSerieTMDB> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SeasonSerieTMDB> seasons) {
        this.seasons = seasons;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public List<VideoTMDB> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoTMDB> videos) {
        this.videos = videos;
    }
}
