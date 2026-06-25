package com.arturviader.pelisbdapi.dto;

import java.util.List;

public record SeriesResponseTMDB(
        int page,
        int total_results,
        int total_pages,
        List<SerieTMDB> results
) {
}

