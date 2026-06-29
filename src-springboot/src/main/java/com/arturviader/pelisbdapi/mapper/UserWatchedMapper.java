package com.arturviader.pelisbdapi.mapper;

import com.arturviader.pelisbdapi.dto.GlobalWatchedItem;
import com.arturviader.pelisbdapi.dto.WatchedItemDto;
import com.arturviader.pelisbdapi.model.*;
import com.arturviader.pelisbdapi.repository.MovieRepository;
import com.arturviader.pelisbdapi.repository.TvRepository;
import com.arturviader.pelisbdapi.service.MovieService;
import com.arturviader.pelisbdapi.service.TvService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class UserWatchedMapper {

    @Autowired
    private MovieService movieService;

    @Autowired
    private TvService tvService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TvRepository tvRepository;

    public WatchedItemDto toDtoWithMovieData(UserWatchedItem item, boolean isInWatchList) {
        String mediaType = item.getType().name();
        if (item.getType().equals(MediaType.movie)) {
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
                    , true
            );
        } else {
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
                    , true
            );
        }

    }

    public WatchedItemDto toGlobalDto(GlobalWatchedItem item) {
        return switch (item.type()) {
            case movie -> {
                Movie movie = movieRepository.findByTmdbId(item.itemId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Movie not found with tmdbId: " + item.itemId()
                        ));

                yield new WatchedItemDto(
                        movie.getTmdbId(),                                   // id
                        "movie",                                             // type
                        item.watchedAt(),                                    // added_at
                        movie.getTitle(),                                    // title
                        movie.getPosterPath(),                               // poster_path
                        movie.getReleaseDate() != null
                                ? movie.getReleaseDate().toString()
                                : null,                                      // release_date
                        "movie",                                             // media_type
                        false,                                               // watchListed
                        true                                                 // watched
                );
            }
            case tv -> {
                Serie serie = tvRepository.findByTmdbId(item.itemId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "TV show not found with tmdbId: " + item.itemId()
                        ));

                yield new WatchedItemDto(
                        serie.getTmdbId(),                                   // id
                        "tv",                                                // type
                        item.watchedAt(),                                    // added_at
                        serie.getTitle(),                                    // title
                        serie.getPosterPath(),                               // poster_path
                        serie.getReleaseDate() != null
                                ? serie.getReleaseDate().toString()
                                : null,                                      // release_date
                        "tv",                                                // media_type
                        false,                                               // watchListed
                        true                                                 // watched
                );
            }
        };
    }

}