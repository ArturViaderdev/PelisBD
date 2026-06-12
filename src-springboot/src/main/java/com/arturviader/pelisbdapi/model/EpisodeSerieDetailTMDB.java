package com.arturviader.pelisbdapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


public class EpisodeSerieDetailTMDB extends EpisodeSerieTMDB {
    @JsonProperty("still_path")
    private String stillPath;

    @JsonProperty("tv_name")
    private String tvName;

    public EpisodeSerieDetailTMDB(Integer episodeNumber, String name, String overview, String airDate, String stillPath, String tvName) {
        super(episodeNumber, name, overview, airDate);
        this.stillPath = stillPath;
        this.tvName = tvName;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }
}
