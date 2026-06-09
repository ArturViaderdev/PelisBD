package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.MovieTMDB;

import java.util.List;

public record MovieResponseTMDB(
        int page,
        int total_results,
        int total_pages,
        List<MovieTMDB> results
){
}
