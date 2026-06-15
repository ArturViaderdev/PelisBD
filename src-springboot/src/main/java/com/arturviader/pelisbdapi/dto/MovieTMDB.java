package com.arturviader.pelisbdapi.dto;

import lombok.Data;

@Data
public class MovieTMDB {
    private Long id;
    private String title;
    private String release_date;
    private String overview;

    public MovieTMDB(Long id, String title, String release_date, String overview, String poster_path) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.overview = overview;
        this.poster_path = poster_path;
    }

    private String poster_path;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRelease_date() { return release_date; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPoster_path() { return poster_path; }
    public void setPoster_path(String poster_path) { this.poster_path = poster_path; }
}
