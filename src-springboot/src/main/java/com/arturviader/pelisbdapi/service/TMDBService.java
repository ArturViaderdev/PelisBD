package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.model.User;

import java.util.List;

public interface TMDBService {
    MovieDetailTMDB getMovieDetailWithTrailer(Long movieId);
    MoviesResponseTMDB getPopularMovies(int page, User user);
    MoviesResponseTMDB getTrendingMovies(String timeWindow, int page, User user);
    SeriesResponseTMDB getPopularTvShows(int page, User user);
    SeriesResponseTMDB getTrendingTvShows(String timeWindow, int page, User user);
    MoviesResponseTMDB searchMovie(String query, int page,User user);
    SeriesResponseTMDB searchTvShows(String query, int page, User user);
    SerieDetailTMDB getTvShowDetailWithTrailer(Long showId);
    String getFirstTrailerKey(List<VideoTMDB> videos);
    SeasonSerieDetailTMDB getSeasonDetail(Long tvId, Integer seasonNumber);
    EpisodeSerieDetailTMDB getEpisodeDetail(Long tvId, Integer seasonNumber, Integer episodeNumber);
    List<VideoTMDB> getMovieVideos(Long movieId);
    List<VideoTMDB> getTvShowVideos(Long tvId);
    GenreDetailMoviesTMDB getGenreDetail(Long genreId, int page, int limit, User user);
    GenreDetailSeriesTMDB getGenreDetailSeries(Long genreId, int page, int limit, User user);
    List<Genre> getAllGenres();
    List<Genre> getAllTVGenres();
    MovieDetailTMDB getMovieById(Long id);
    SerieDetailTMDB getTvShowDetail(Long id);
}