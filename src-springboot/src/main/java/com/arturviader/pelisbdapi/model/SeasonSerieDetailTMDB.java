package com.arturviader.pelisbdapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SeasonSerieDetailTMDB extends SeasonSerieTMDB{
    private List<EpisodeSerieTMDB> episodes;
    private String posterPath;
    private String overview;
    @JsonProperty("tv_name")
    private String tvName;

    public SeasonSerieDetailTMDB(Integer seasonNumber, String airDate, String name, String episodeCount, List<EpisodeSerieTMDB> episodes, String posterPath, String overview) {
        super(seasonNumber, airDate, name, episodeCount);
        this.episodes = episodes;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<EpisodeSerieTMDB> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeSerieTMDB> episodes) {
        this.episodes = episodes;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
