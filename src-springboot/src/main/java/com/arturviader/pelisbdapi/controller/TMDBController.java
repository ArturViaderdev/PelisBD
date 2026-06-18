package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.exception.NoUserAuthenticated;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.TMDBService;
import com.arturviader.pelisbdapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TMDBController {
    @Autowired
    private TMDBService tmdbService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/search/movie")
    public ResponseEntity<MoviesResponseTMDB> searchMovies(@RequestParam String query,
                               @RequestParam (defaultValue = "1") int page) {
        User user = getCurrentUser();
        return ResponseEntity.ok(tmdbService.searchMovie(query,page,user));
    }

    @GetMapping("/api/movies/{id}")
    public ResponseEntity<MovieDetailTMDB> getMovieById(@PathVariable Long id) {
        MovieDetailTMDB movie = tmdbService.getMovieDetailWithTrailer(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/api/movies/popular")
    public ResponseEntity<MoviesResponseTMDB> getPopularMovies(
            @RequestParam(defaultValue = "1") int page) {

        User user = getCurrentUser();
        MoviesResponseTMDB movies = tmdbService.getPopularMovies(page,user);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/api/movies/trending")
    public ResponseEntity<MoviesResponseTMDB> getTrendingMovies(
            @RequestParam(defaultValue = "day") String timeWindow,
            @RequestParam(defaultValue = "1") int page) {
        User user = getCurrentUser();
        MoviesResponseTMDB movies = tmdbService.getTrendingMovies(timeWindow, page,user);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/api/tv/popular")
    public ResponseEntity<SeriesResponseTMDB> getPopularTvShows(
            @RequestParam(defaultValue = "1") int page) {
        User user = getCurrentUser();
        SeriesResponseTMDB shows = tmdbService.getPopularTvShows(page,user);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/api/tv/trending")
    public ResponseEntity<SeriesResponseTMDB> getTrendingTvShows(
            @RequestParam(defaultValue = "day") String timeWindow,
            @RequestParam(defaultValue = "1") int page) {
        User user = getCurrentUser();
        SeriesResponseTMDB shows = tmdbService.getTrendingTvShows(timeWindow, page,user);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/api/search/tv")
    public ResponseEntity<SeriesResponseTMDB> searchTvShows(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        User user = getCurrentUser();
        return ResponseEntity.ok(tmdbService.searchTvShows(query, page,user));
    }

    @GetMapping("/api/search/multi")
    public ResponseEntity<SearchResultsMoviesAndTV> searchAll(
            @RequestParam String query
    )
    {
        User user = getCurrentUser();
        MoviesResponseTMDB results = tmdbService.searchMovie(query,1, user);
        SeriesResponseTMDB resultstv = tmdbService.searchTvShows(query, 1,user);
        SearchResultsMoviesAndTV all = new SearchResultsMoviesAndTV(results.results(),resultstv.results());
        return ResponseEntity.ok(all);
    }

    @GetMapping("/api/tv/{id}")
    public ResponseEntity<SerieDetailTMDB> getTvShowDetail(@PathVariable Long id) {
        SerieDetailTMDB show = tmdbService.getTvShowDetailWithTrailer(id);
        if (show == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(show);
    }

    @GetMapping("/api/tv/{id}/season/{seasonNumber}")
    public ResponseEntity<SeasonSerieDetailTMDB> getSeasonDetail(
            @PathVariable Long id,
            @PathVariable Integer seasonNumber) {
        SeasonSerieDetailTMDB season = tmdbService.getSeasonDetail(id, seasonNumber);
        if (season == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(season);
    }

    @GetMapping("/api/tv/{id}/season/{seasonNumber}/episode/{episodeNumber}")
    public ResponseEntity<EpisodeSerieDetailTMDB> getEpisodeDetail(
            @PathVariable Long id,
            @PathVariable Integer seasonNumber,
            @PathVariable Integer episodeNumber) {
        EpisodeSerieDetailTMDB episode = tmdbService.getEpisodeDetail(id, seasonNumber, episodeNumber);
        if (episode == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(episode);
    }

    @GetMapping("/api/movies/{id}/videos")
    public ResponseEntity<List<VideoTMDB>> getMovieVideos(@PathVariable Long id) {
        List<VideoTMDB> videos = tmdbService.getMovieVideos(id);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/api/tv/{id}/videos")
    public ResponseEntity<List<VideoTMDB>> getTvShowVideos(@PathVariable Long id) {
        List<VideoTMDB> videos = tmdbService.getTvShowVideos(id);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/api/movies/category/{id}")
    public ResponseEntity<GenreDetailMoviesTMDB> getGenreDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page) {
        int limit = 20;
        User user = getCurrentUser();
        GenreDetailMoviesTMDB detail = tmdbService.getGenreDetail(id, page, limit, user);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/api/tv/category/{id}")
    public ResponseEntity<GenreDetailSeriesTMDB> getGenreDetailSeries(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        User user = getCurrentUser();
        GenreDetailSeriesTMDB detail = tmdbService.getGenreDetailSeries(id, page, limit,user);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping ("/api/movies/categories")
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(tmdbService.getAllGenres());
    }

    @GetMapping ("/api/tv/categories")
    public ResponseEntity<List<Genre>> getAllTVGenres() {
        return ResponseEntity.ok(tmdbService.getAllTVGenres());
    }

    public User getCurrentUser() {
        System.out.println("getcurrent");
        try {
            User user = userService.getCurrentUser();
            return user;
        }
        catch(NoUserAuthenticated | UsernameNotFoundException ex)
        {
            return null;
        }
    }

   /* private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null; // ✅ No autenticado
    }*/
}
