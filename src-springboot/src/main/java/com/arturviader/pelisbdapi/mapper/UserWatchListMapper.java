package com.arturviader.pelisbdapi.mapper;

import com.arturviader.pelisbdapi.dto.WatchlistItemDto;
import com.arturviader.pelisbdapi.model.*;
import com.arturviader.pelisbdapi.service.MovieService;
import com.arturviader.pelisbdapi.service.TvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class UserWatchListMapper {
    @Autowired
    private MovieService movieService;

    @Autowired
    private TvService tvService;

    public WatchlistItemDto toDtoWithMovieData(UserWatchlistItem item, boolean watched) {
        String mediaType = item.getType().name();
        if (item.getType().equals(MediaType.movie)) {
            var movieOpt = movieService.findById(item.getItemId());
            return new WatchlistItemDto(
                    movieOpt.map(Movie::getTmdbId).orElse(null),
                    item.getType().name(),
                    item.getAddedAt(),
                    movieOpt.map(Movie::getTitle).orElse("Unknown"),
                    movieOpt.map(Movie::getPosterPath).orElse("/no-poster.jpg"),
                    movieOpt.map(Movie::getReleaseDate)
                            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .orElse(null), // ✅ release_date
                    mediaType,
                    true,
                    watched
            );

        } else {
            var tvOpt = tvService.findById(item.getItemId());
            return new WatchlistItemDto(
                    tvOpt.map(Serie::getTmdbId).orElse(null),
                    item.getType().name(),
                    item.getAddedAt(),
                    tvOpt.map(Serie::getTitle).orElse("Unknown"),
                    tvOpt.map(Serie::getPosterPath).orElse("/no-poster.jpg"),
                    tvOpt.map(Serie::getReleaseDate)
                            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .orElse(null), // ✅ release_date
                    mediaType,
                    true,
                    watched
            );
        }
    }
}
