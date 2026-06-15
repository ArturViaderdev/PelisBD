package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.Movie;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import com.arturviader.pelisbdapi.model.UserWatchlistItem;
import com.arturviader.pelisbdapi.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class UserWatchListMapper {
    @Autowired
    private MovieService movieService;

    public WatchlistItemDto toDtoWithMovieData(UserWatchlistItem item) {
        String mediaType = item.getType().name();
        var movieOpt = movieService.findById(item.getItemId(), mediaType);
        return new WatchlistItemDto(
                movieOpt.map(Movie::getTmdbId).orElse(null), // ✅ id = tmdbId
                item.getType().name(),
                item.getAddedAt(),
                movieOpt.map(Movie::getTitle).orElse("Unknown"),
                movieOpt.map(Movie::getPosterPath).orElse("/no-poster.jpg"),
                movieOpt.map(Movie::getReleaseDate)
                        .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .orElse(null), // ✅ release_date
                mediaType // ✅ media_type
        );
    }
}
