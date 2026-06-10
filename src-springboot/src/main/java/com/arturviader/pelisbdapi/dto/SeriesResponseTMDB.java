package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MovieTMDB;
import com.arturviader.pelisbdapi.model.SerieTMDB;
import lombok.Data;

import java.util.List;

public record SeriesResponseTMDB (
    int page,
    int total_results,
    int total_pages,
    List<SerieTMDB> results
){
    }

