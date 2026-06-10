package com.arturviader.pelisbdapi.model;

import java.util.List;

public class GenreDetailSeriesTMDB {
    private Long id;
    private String name;
    private List<SerieTMDB> series;

    public GenreDetailSeriesTMDB(Long id, String name, List<SerieTMDB> series) {
        this.id = id;
        this.name = name;
        this.series = series;
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

    public List<SerieTMDB> getSeries() {
        return series;
    }

    public void setSeries(List<SerieTMDB> series) {
        this.series = series;
    }
}
