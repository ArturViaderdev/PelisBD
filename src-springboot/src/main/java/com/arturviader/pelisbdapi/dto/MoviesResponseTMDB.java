package com.arturviader.pelisbdapi.dto;

import java.util.List;

public record MoviesResponseTMDB(
        int page,
        int total_results,
        int total_pages,
        List<MovieTMDB> results
) {
}
