package com.arturviader.pelisbdapi.model;

import lombok.Data;

import java.util.List;

@Data
public class GenreDetailMoviesTMDB {
    private Long id;
    private String name;
    private int page;
    private int total_results;
    private int total_pages;
    private List<MovieTMDB> results;

    public GenreDetailMoviesTMDB(Long id, String name, int page, int total_results, int total_pages, List<MovieTMDB> results) {
        this.id = id;
        this.name = name;
        this.page = page;
        this.total_results = total_results;
        this.total_pages = total_pages;
        this.results = results;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<MovieTMDB> getResults() {
        return results;
    }

    public void setResults(List<MovieTMDB> results) {
        this.results = results;
    }
}
