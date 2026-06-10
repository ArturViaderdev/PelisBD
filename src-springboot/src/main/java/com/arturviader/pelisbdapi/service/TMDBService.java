package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.GenreListResponseTMDB;
import com.arturviader.pelisbdapi.dto.MoviesResponseTMDB;
import com.arturviader.pelisbdapi.dto.SeriesResponseTMDB;
import com.arturviader.pelisbdapi.exception.IncorrectTimeWidow;
import com.arturviader.pelisbdapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TMDBService {
    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public MoviesResponseTMDB searchMovie(String query) {
        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&api_key=" + apiKey;

        ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
        return response.getBody();
    }

    public MovieDetailTMDB getMovieDetailWithTrailer(Long movieId) {
        MovieDetailTMDB detail = getMovieById(movieId); // Detalle básico
        if (detail == null) return null;

        List<VideoTMDB> videos = getMovieVideos(movieId);
        String trailerKey = getFirstTrailerKey(videos);
        detail.setTrailerKey(trailerKey);
        detail.setVideos(videos);
        return detail;
    }

    private MovieDetailTMDB getMovieById(Long id) {
        String url = "https://api.themoviedb.org/3/movie/" + id +
                "?api_key=" + apiKey + "&language=es-ES";

        ResponseEntity<MovieDetailTMDB> response = restTemplate.getForEntity(url, MovieDetailTMDB.class);
        return response.getBody();
    }

    public List<MovieTMDB> getPopularMovies(int page) {
        String url = "https://api.themoviedb.org/3/movie/popular" +
                "?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<MovieTMDB[]> response = restTemplate.getForEntity(url, MovieTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<MovieTMDB> getTrendingMovies(String timeWindow, int page) {
        if (!"day".equals(timeWindow) && !"week".equals(timeWindow)) {
            throw new IncorrectTimeWidow();
        }
        String url = "https://api.themoviedb.org/3/trending/movie/" + timeWindow +
                "?api_key=" + apiKey + "&language=es-ES&page=1";
        try {
            ResponseEntity<MovieTMDB[]> response = restTemplate.getForEntity(url, MovieTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<SerieTMDB> getPopularTvShows(int page) {
        String url = "https://api.themoviedb.org/3/tv/popular?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<SerieTMDB[]> response = restTemplate.getForEntity(url, SerieTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<SerieTMDB> getTrendingTvShows(String timeWindow, int page) {
        if (!"day".equals(timeWindow) && !"week".equals(timeWindow)) {
            throw new IllegalArgumentException("timeWindow debe ser 'day' o 'week'");
        }
        String url = "https://api.themoviedb.org/3/trending/tv/" + timeWindow +
                "?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<SerieTMDB[]> response = restTemplate.getForEntity(url, SerieTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<SerieTMDB> searchTvShows(String query, int page) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        String url = "https://api.themoviedb.org/3/search/tv?api_key=" + apiKey +
                "&language=es-ES&query=" + query + "&page=" + page;
        try {
            ResponseEntity<SerieTMDB[]> response = restTemplate.getForEntity(url, SerieTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    private SerieDetailTMDB getTvShowDetail(Long id) {
        String url = "https://api.themoviedb.org/3/tv/" + id +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SerieDetailTMDB> response = restTemplate.getForEntity(url, SerieDetailTMDB.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public SerieDetailTMDB getTvShowDetailWithTrailer(Long showId) {
        SerieDetailTMDB detail = getTvShowDetail(showId); // Detalle básico
        if (detail == null) return null;

        List<VideoTMDB> videos = getTvShowVideos(showId);
        String trailerKey = getFirstTrailerKey(videos);
        detail.setTrailerKey(trailerKey);
        detail.setVideos(videos);
        return detail;
    }

    public String getFirstTrailerKey(List<VideoTMDB> videos) {
        // Paso 1: Buscar trailer de YouTube con "Español" y "Trailer" en el nombre
        return videos.stream()
                .filter(v -> "YouTube".equals(v.getSite()))  // Solo YouTube
                .filter(v -> v.getName() != null)
                .filter(v -> v.getName().toLowerCase().contains("español") ||
                        v.getName().toLowerCase().contains("spanish"))
                .filter(v -> v.getName().toLowerCase().contains("trailer") ||
                        v.getName().toLowerCase().contains("tráiler"))
                .findFirst()
                .map(VideoTMDB::getKey)
                .orElseGet(() -> {
                    // Paso 2: Si no hay "Español + Trailer", buscar cualquier trailer de YouTube
                    return videos.stream()
                            .filter(v -> "YouTube".equals(v.getSite()))
                            .filter(v -> v.getName() != null)
                            .filter(v -> "Trailer".equals(v.getType()) ||
                                    v.getName().toLowerCase().contains("trailer") ||
                                    v.getName().toLowerCase().contains("tráiler"))
                            .findFirst()
                            .map(VideoTMDB::getKey)
                            .orElse(null);

                });
    }

    public SeasonSerieDetailTMDB getSeasonDetail(Long tvId, Integer seasonNumber) {
        String url = "https://api.themoviedb.org/3/tv/" + tvId + "/season/" + seasonNumber +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SeasonSerieDetailTMDB> response = restTemplate.getForEntity(url, SeasonSerieDetailTMDB.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public EpisodeSerieDetailTMDB getEpisodeDetail(Long tvId, Integer seasonNumber, Integer episodeNumber) {
        String url = "https://api.themoviedb.org/3/tv/" + tvId + "/season/" + seasonNumber +
                "/episode/" + episodeNumber +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<EpisodeSerieDetailTMDB> response = restTemplate.getForEntity(url, EpisodeSerieDetailTMDB.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public List<VideoTMDB> getMovieVideos(Long movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/videos" +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<VideoTMDB[]> response = restTemplate.getForEntity(url, VideoTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<VideoTMDB> getTvShowVideos(Long tvId) {
        String url = "https://api.themoviedb.org/3/tv/" + tvId + "/videos" +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<VideoTMDB[]> response = restTemplate.getForEntity(url, VideoTMDB[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            return List.of();
        }
    }

    public GenreDetailMoviesTMDB getGenreDetail(Long genreId, int page, int limit) {
        // Primero, obtener el nombre del género
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<GenreListResponseTMDB> response = restTemplate.getForEntity(url, GenreListResponseTMDB.class);
            if (response.getBody() == null) return null;
            Genre genre = response.getBody().getGenres().stream()
                    .filter(g -> g.id().equals(genreId))
                    .findFirst()
                    .orElse(null);

            if (genre == null) return null;
            List<MovieTMDB> movies = getMoviesByGenre(genreId, page, limit);
            GenreDetailMoviesTMDB detail = new GenreDetailMoviesTMDB(genreId,genre.name(),movies);
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    public GenreDetailSeriesTMDB getGenreDetailSeries(Long genreId, int page, int limit) {
        // Primero, obtener el nombre del género
        String url = "https://api.themoviedb.org/3/genre/tv/list?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<GenreListResponseTMDB> response = restTemplate.getForEntity(url, GenreListResponseTMDB.class);
            if (response.getBody() == null) return null;
            Genre genre = response.getBody().getGenres().stream()
                    .filter(g -> g.id().equals(genreId))
                    .findFirst()
                    .orElse(null);

            if (genre == null) return null;
            List<SerieTMDB> series = getSeriesByGenre(genreId, page, limit);
            GenreDetailSeriesTMDB detail = new GenreDetailSeriesTMDB(genreId,genre.name(),series);
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    private List<MovieTMDB> getMoviesByGenre(Long genreId, int page, int limit) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&with_genres=" + genreId +
                "&sort_by=popularity.desc" +
                "&page=" + page +
                "&language=es-ES" +
                "&limit=" + limit; // ✅ Añadimos limit

        try {
            ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
            return response.getBody() != null ? response.getBody().results() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<SerieTMDB> getSeriesByGenre(Long genreId, int page, int limit) {
        String url = "https://api.themoviedb.org/3/discover/tv?api_key=" + apiKey +
                "&with_genres=" + genreId +
                "&sort_by=popularity.desc" +
                "&page=" + page +
                "&language=es-ES" +
                "&limit=" + limit; // ✅ Añadimos limit

        try {
            ResponseEntity<SeriesResponseTMDB> response = restTemplate.getForEntity(url, SeriesResponseTMDB.class);
            return response.getBody() != null ? response.getBody().results() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Genre> getAllGenres() {
        String movieUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=es-ES";
        List<Genre> movieGenres = getGenresFromUrl(movieUrl);
        String tvUrl = "https://api.themoviedb.org/3/genre/tv/list?api_key=" + apiKey + "&language=es-ES";
        List<Genre> tvGenres = getGenresFromUrl(tvUrl);
        List<Genre> allGenres = new ArrayList<>();
        allGenres.addAll(movieGenres);
        allGenres.addAll(tvGenres);
        return allGenres.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Genre> getGenresFromUrl(String url) {
        try {
            ResponseEntity<GenreListResponseTMDB> response = restTemplate.getForEntity(url, GenreListResponseTMDB.class);
            return response.getBody() != null ? response.getBody().getGenres() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
