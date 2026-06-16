package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.*;
import com.arturviader.pelisbdapi.service.MovieService;
import com.arturviader.pelisbdapi.service.TvService;
import com.arturviader.pelisbdapi.service.UserMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class UserWatchedMapper {

    @Autowired
    private MovieService movieService;

    @Autowired
    private TvService tvService;

    public WatchedItemDto toDtoWithMovieData(UserWatchedItem item, boolean isInWatchList) {
        String mediaType = item.getType().name();
        if(item.getType().equals(MediaType.movie))
        {
            var movieOpt = movieService.findById(item.getItemId());

            return new WatchedItemDto(
                    movieOpt.map(Movie::getTmdbId).orElse(null), // ✅ id = tmdbId
                    item.getType().name(), // ✅ type
                    item.getWatchedAt(), // ✅ added_at
                    movieOpt.map(Movie::getTitle).orElse("Unknown"), // ✅ title
                    movieOpt.map(Movie::getPosterPath).orElse("/no-poster.jpg"), // ✅ poster_path
                    movieOpt.map(Movie::getReleaseDate)
                            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .orElse(null), // ✅ release_date
                    mediaType,
                    isInWatchList
                    ,true
            );
        }
        else
        {
            var tvOpt = tvService.findById(item.getItemId());
            return new WatchedItemDto(
                    tvOpt.map(Serie::getTmdbId).orElse(null), // ✅ id = tmdbId
                    item.getType().name(), // ✅ type
                    item.getWatchedAt(), // ✅ added_at
                    tvOpt.map(Serie::getTitle).orElse("Unknown"), // ✅ title
                    tvOpt.map(Serie::getPosterPath).orElse("/no-poster.jpg"), // ✅ poster_path
                    tvOpt.map(Serie::getReleaseDate)
                            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .orElse(null), // ✅ release_date
                    mediaType // ✅ media_type
                    ,
                    isInWatchList
                    ,true
            );
        }

    }
}
