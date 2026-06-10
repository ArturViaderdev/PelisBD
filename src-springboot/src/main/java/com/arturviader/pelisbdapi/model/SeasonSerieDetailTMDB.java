package com.arturviader.pelisbdapi.model;

import lombok.Data;

import java.util.List;

@Data
public class SeasonSerieDetailTMDB extends SeasonSerieTMDB{
    private List<EpisodeSerieDetailTMDB> episodes;
    private String posterPath;

    public SeasonSerieDetailTMDB(Integer seasonNumber, String airDate, String name, String overview, List<EpisodeSerieDetailTMDB> episodes, String posterPath) {
        super(seasonNumber, airDate, name, overview);
        this.episodes = episodes;
        this.posterPath = posterPath;
    }

    public List<EpisodeSerieDetailTMDB> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeSerieDetailTMDB> episodes) {
        this.episodes = episodes;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
