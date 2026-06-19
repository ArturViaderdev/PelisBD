package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.exception.IncorrectTimeWidow;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TMDBServiceImpl implements TMDBService {
    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    @Lazy
    private UserMediaService userMediaService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserEpisodeProgressService userEpisodeProgressService;

    @Override
    public MovieDetailTMDB getMovieDetailWithTrailer(Long movieId) {
        MovieDetailTMDB detail = getMovieById(movieId); // Detalle básico
        if (detail == null) return null;

        List<VideoTMDB> videos = getMovieVideos(movieId);
        String trailerKey = getFirstTrailerKey(videos);
        detail.setTrailerKey(trailerKey);
        detail.setVideos(videos);
        return detail;
    }

    @Override
    public MovieDetailTMDB getMovieById(Long id) {
        String url = "https://api.themoviedb.org/3/movie/" + id +
                "?api_key=" + apiKey + "&language=es-ES";

        ResponseEntity<MovieDetailTMDB> response = restTemplate.getForEntity(url, MovieDetailTMDB.class);
        return response.getBody();
    }

    @Override
    public MoviesResponseTMDB getPopularMovies(int page, User user) {
        String url = "https://api.themoviedb.org/3/movie/popular" +
                "?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToMovies(response.getBody().results(), user);
            }

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private void addWatchStatusToMovies(List<MovieTMDB> movies, User user) {
        for (MovieTMDB movie : movies) {
            movie.setWatched(userMediaService.isMovieWatched(movie.getId(), user.getUserName(), MediaType.movie));
            movie.setWatchListed(userMediaService.isMovieInWatchlist(user.getUserName(), movie.getId(), MediaType.movie));
        }
    }

    private void addWatchStatusToTv(List<SerieTMDB> series, User user) {
        for (SerieTMDB serie : series) {
            serie.setWatched(userMediaService.isMovieWatched(serie.getId(), user.getUserName(), MediaType.tv));
            serie.setWatchListed(userMediaService.isMovieInWatchlist(user.getUserName(), serie.getId(), MediaType.tv));
        }
    }

    @Override
    public MoviesResponseTMDB getTrendingMovies(String timeWindow, int page, User user) {
        if (!"day".equals(timeWindow) && !"week".equals(timeWindow)) {
            throw new IncorrectTimeWidow();
        }
        String url = "https://api.themoviedb.org/3/trending/movie/" + timeWindow +
                "?api_key=" + apiKey + "&language=es-ES&page=1";
        try {
            ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToMovies(response.getBody().results(), user);
            }
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SeriesResponseTMDB getPopularTvShows(int page, User user) {
        String url = "https://api.themoviedb.org/3/tv/popular?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<SeriesResponseTMDB> response = restTemplate.getForEntity(url, SeriesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToTv(response.getBody().results(), user);
            }

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SeriesResponseTMDB getTrendingTvShows(String timeWindow, int page, User user) {
        if (!"day".equals(timeWindow) && !"week".equals(timeWindow)) {
            throw new IllegalArgumentException("timeWindow debe ser 'day' o 'week'");
        }
        String url = "https://api.themoviedb.org/3/trending/tv/" + timeWindow +
                "?api_key=" + apiKey + "&language=es-ES&page=" + page;
        try {
            ResponseEntity<SeriesResponseTMDB> response = restTemplate.getForEntity(url, SeriesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToTv(response.getBody().results(), user);
            }
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public MoviesResponseTMDB searchMovie(String query, int page, User user) {
        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&page=" + page + "&api_key=" + apiKey;
        try {
            ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToMovies(response.getBody().results(), user);
            }
            return response.getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public SeriesResponseTMDB searchTvShows(String query, int page, User user) {
        String url = "https://api.themoviedb.org/3/search/tv?api_key=" + apiKey +
                "&language=es-ES&query=" + query + "&page=" + page;
        try {
            ResponseEntity<SeriesResponseTMDB> response = restTemplate.getForEntity(url, SeriesResponseTMDB.class);
            if (user != null) {
                addWatchStatusToTv(response.getBody().results(), user);
            }
            return response.getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public SerieDetailTMDB getTvShowDetail(Long id) {
        String url = "https://api.themoviedb.org/3/tv/" + id +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SerieDetailTMDB> response = restTemplate.getForEntity(url, SerieDetailTMDB.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SerieDetailTMDB getTvShowDetailWithTrailer(Long showId) {
        SerieDetailTMDB detail = getTvShowDetail(showId);
        if (detail == null) return null;

        List<VideoTMDB> videos = getTvShowVideos(showId);
        String trailerKey = getFirstTrailerKey(videos);
        detail.setTrailerKey(trailerKey);
        detail.setVideos(videos);
        return detail;
    }

    @Override
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

    @Override
    public SeasonSerieDetailTMDB getSeasonDetail(Long tvId, Integer seasonNumber) {
        String tvName = "";
        String url = "https://api.themoviedb.org/3/tv/" + tvId +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SerieDetailTMDB> response = restTemplate.getForEntity(url, SerieDetailTMDB.class);
            tvName = response.getBody().getName();
        } catch (Exception e) {
            tvName = "";
        }

        url = "https://api.themoviedb.org/3/tv/" + tvId + "/season/" + seasonNumber +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SeasonSerieDetailTMDB> response = restTemplate.getForEntity(url, SeasonSerieDetailTMDB.class);
            SeasonSerieDetailTMDB detail = response.getBody();
            detail.setTvName(tvName);
            List<EpisodeSerieTMDB> episodes = detail.getEpisodes();
            User user = userService.getCurrentUser();
            if (user != null) {
                for (EpisodeSerieTMDB episode : episodes) {
                    episode.setWatched(userEpisodeProgressService.getEpisodeWatched(user.getId(), tvId, seasonNumber, episode.getEpisodeNumber()));
                }
                detail.setEpisodes(episodes);
            }

            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public EpisodeSerieDetailTMDB getEpisodeDetail(Long tvId, Integer seasonNumber, Integer episodeNumber) {
        String tvName = "";
        String url = "https://api.themoviedb.org/3/tv/" + tvId +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<SerieDetailTMDB> response = restTemplate.getForEntity(url, SerieDetailTMDB.class);
            tvName = response.getBody().getName();
        } catch (Exception e) {
            tvName = "";
        }

        url = "https://api.themoviedb.org/3/tv/" + tvId + "/season/" + seasonNumber +
                "/episode/" + episodeNumber +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<EpisodeSerieDetailTMDB> response = restTemplate.getForEntity(url, EpisodeSerieDetailTMDB.class);
            EpisodeSerieDetailTMDB detail = response.getBody();
            detail.setTvName(tvName);
            User user = userService.getCurrentUser();
            if (user != null) {
                detail.setWatched(userEpisodeProgressService.getEpisodeWatched(user.getId(), tvId, seasonNumber, episodeNumber));
            }
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<VideoTMDB> getMovieVideos(Long movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/videos" +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<VideosResponseDTO> response = restTemplate.getForEntity(url, VideosResponseDTO.class);
            return response.getBody() != null ? response.getBody().results() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<VideoTMDB> getTvShowVideos(Long tvId) {
        String url = "https://api.themoviedb.org/3/tv/" + tvId + "/videos" +
                "?api_key=" + apiKey + "&language=es-ES";
        try {
            ResponseEntity<VideosResponseDTO> response = restTemplate.getForEntity(url, VideosResponseDTO.class);
            return response.getBody() != null ? response.getBody().results() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public GenreDetailMoviesTMDB getGenreDetail(Long genreId, int page, int limit, User user) {
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
            GenreDetailMoviesTMDB movies = getMoviesByGenre(genreId, page, limit, genre.name());
            if (user != null) {
                addWatchStatusToMovies(movies.getResults(), user);
            }

            return movies;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public GenreDetailSeriesTMDB getGenreDetailSeries(Long genreId, int page, int limit, User user) {
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
            GenreDetailSeriesTMDB series = getSeriesByGenre(genreId, page, limit, genre.name());
            if (user != null) {
                addWatchStatusToTv(series.getResults(), user);
            }
            return series;
        } catch (Exception e) {
            return null;
        }
    }

    private GenreDetailMoviesTMDB getMoviesByGenre(Long genreId, int page, int limit, String name) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&with_genres=" + genreId +
                "&sort_by=popularity.desc" +
                "&page=" + page +
                "&language=es-ES" +
                "&limit=" + limit; // ✅ Añadimos limit

        try {
            ResponseEntity<MoviesResponseTMDB> response = restTemplate.getForEntity(url, MoviesResponseTMDB.class);
            GenreDetailMoviesTMDB detail = new GenreDetailMoviesTMDB(genreId, name, page, response.getBody().total_results(), response.getBody().total_pages(), response.getBody().results());
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    private GenreDetailSeriesTMDB getSeriesByGenre(Long genreId, int page, int limit, String name) {
        String url = "https://api.themoviedb.org/3/discover/tv?api_key=" + apiKey +
                "&with_genres=" + genreId +
                "&sort_by=popularity.desc" +
                "&page=" + page +
                "&language=es-ES" +
                "&limit=" + limit; // ✅ Añadimos limit

        try {
            ResponseEntity<SeriesResponseTMDB> response = restTemplate.getForEntity(url, SeriesResponseTMDB.class);
            GenreDetailSeriesTMDB results = new GenreDetailSeriesTMDB(genreId, name, page, response.getBody().total_results(), response.getBody().total_pages(), response.getBody().results());
            return results;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String movieUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + apiKey + "&language=es-ES";
        List<Genre> movieGenres = getGenresFromUrl(movieUrl);
        return movieGenres;
    }

    @Override
    public List<Genre> getAllTVGenres() {
        String tvUrl = "https://api.themoviedb.org/3/genre/tv/list?api_key=" + apiKey + "&language=es-ES";
        List<Genre> tvGenres = getGenresFromUrl(tvUrl);
        return tvGenres;
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

