package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.Movie;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import com.arturviader.pelisbdapi.model.UserWatchlistItem;
import com.arturviader.pelisbdapi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class UserWatchedMapper {

    @Autowired
    private MovieService movieService;

    public WatchedItemDto toDtoWithMovieData(UserWatchedItem item) {
        String mediaType = item.getType().name();

        // Busca la película por su ID interno (itemId) → devuelve Movie
        var movieOpt = movieService.findById(item.getItemId(), mediaType);

        return new WatchedItemDto(
                movieOpt.map(Movie::getTmdbId).orElse(null), // ✅ id = tmdbId
                item.getType().name(), // ✅ type
                item.getWatchedAt(), // ✅ added_at
                movieOpt.map(Movie::getTitle).orElse("Unknown"), // ✅ title
                movieOpt.map(Movie::getPosterPath).orElse("/no-poster.jpg"), // ✅ poster_path
                movieOpt.map(Movie::getReleaseDate)
                        .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .orElse(null), // ✅ release_date
                mediaType // ✅ media_type
        );
    }
}
