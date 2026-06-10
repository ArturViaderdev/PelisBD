package com.arturviader.pelisbdapi.model;

import lombok.Data;

@Data
public class EpisodeSerieDetailTMDB {
    private Integer episodeNumber;
    private String name;
    private String overview;
    private String airDate;
    private Double voteAverage;
    private String stillPath;
}
