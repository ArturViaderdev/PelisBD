package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.controller.TMDBController;
import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.security.JwtUserDetailsService;
import com.arturviader.pelisbdapi.service.JwtService;
import com.arturviader.pelisbdapi.service.TMDBService;
import com.arturviader.pelisbdapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TMDBController.class)
@AutoConfigureMockMvc(addFilters = false)
class TMDBControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TMDBService tmdbService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtUserDetailsService jwtUserDetailsService;


    private User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("artur2");
        return user;
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void searchMovies_shouldReturnOk() throws Exception {
        User user = mockUser();
        MoviesResponseTMDB response = new MoviesResponseTMDB(
                1,
                1,
                1,
                List.of(new MovieTMDB(1L, "Batman", "2024-01-01", "overview", "/poster.jpg", 8.1))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.searchMovie("batman", 1, user)).thenReturn(response);

        mockMvc.perform(get("/api/search/movie")
                        .param("query", "batman")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total_results").value(1))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.results[0].id").value(1))
                .andExpect(jsonPath("$.results[0].title").value("Batman"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getMovieById_shouldReturnOk_whenMovieExists() throws Exception {
        MovieDetailTMDB movie = new MovieDetailTMDB(
                123L,
                "Batman",
                "2024-01-01",
                "overview",
                "/poster.jpg",
                8.1,
                "/backdrop.jpg",
                "en",
                List.of(),
                List.of(),
                "trailer123",
                1000
        );
        when(tmdbService.getMovieDetailWithTrailer(123L)).thenReturn(movie);
        mockMvc.perform(get("/api/movies/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.title").value("Batman"))
                .andExpect(jsonPath("$.trailerKey").value("trailer123"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getMovieById_shouldReturnNotFound_whenMovieDoesNotExist() throws Exception {
        when(tmdbService.getMovieDetailWithTrailer(123L)).thenReturn(null);
        mockMvc.perform(get("/api/movies/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getPopularMovies_shouldReturnOk() throws Exception {
        User user = mockUser();
        MoviesResponseTMDB response = new MoviesResponseTMDB(
                1,
                1,
                1,
                List.of(new MovieTMDB(1L, "Movie 1", "2024-01-01", "overview", "/poster.jpg", 7.5))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getPopularMovies(1, user)).thenReturn(response);
        mockMvc.perform(get("/api/movies/popular").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Movie 1"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getTrendingMovies_shouldReturnOk() throws Exception {
        User user = mockUser();
        MoviesResponseTMDB response = new MoviesResponseTMDB(
                1,
                1,
                1,
                List.of(new MovieTMDB(1L, "Trending Movie", "2024-01-01", "overview", "/poster.jpg", 7.9))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getTrendingMovies("day", 1, user)).thenReturn(response);
        mockMvc.perform(get("/api/movies/trending")
                        .param("timeWindow", "day")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Trending Movie"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getPopularTvShows_shouldReturnOk() throws Exception {
        User user = mockUser();
        SeriesResponseTMDB response = new SeriesResponseTMDB(
                1,
                1,
                1,
                List.of(new SerieTMDB(1L, "Breaking Bad", "Breaking Bad", "overview", "/poster.jpg", "2008-01-01", 9.5, 100, 99.0))
        );

        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getPopularTvShows(1, user)).thenReturn(response);
        mockMvc.perform(get("/api/tv/popular").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Breaking Bad"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getTrendingTvShows_shouldReturnOk() throws Exception {
        User user = mockUser();
        SeriesResponseTMDB response = new SeriesResponseTMDB(
                1,
                1,
                1,
                List.of(new SerieTMDB(1L, "The Last of Us", "The Last of Us", "overview", "/poster.jpg", "2023-01-01", 8.9, 200, 88.0))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getTrendingTvShows("day", 1, user)).thenReturn(response);
        mockMvc.perform(get("/api/tv/trending")
                        .param("timeWindow", "day")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("The Last of Us"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void searchTvShows_shouldReturnOk() throws Exception {
        User user = mockUser();
        SeriesResponseTMDB response = new SeriesResponseTMDB(
                1,
                1,
                1,
                List.of(new SerieTMDB(1L, "Breaking Bad", "Breaking Bad", "overview", "/poster.jpg", "2008-01-01", 9.5, 100, 99.0))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.searchTvShows("breaking bad", 1, user)).thenReturn(response);
        mockMvc.perform(get("/api/search/tv")
                        .param("query", "breaking bad")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("Breaking Bad"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void searchAll_shouldReturnOk() throws Exception {
        User user = mockUser();
        MoviesResponseTMDB movies = new MoviesResponseTMDB(
                1, 1, 1,
                List.of(new MovieTMDB(1L, "Batman", "2024-01-01", "overview", "/poster.jpg", 8.1))
        );
        SeriesResponseTMDB series = new SeriesResponseTMDB(
                1, 1, 1,
                List.of(new SerieTMDB(2L, "Gotham", "Gotham", "overview", "/poster.jpg", "2014-01-01", 7.8, 100, 50.0))
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.searchMovie("batman", 1, user)).thenReturn(movies);
        when(tmdbService.searchTvShows("batman", 1, user)).thenReturn(series);
        mockMvc.perform(get("/api/search/multi")
                        .param("query", "batman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movies[0].title").value("Batman"))
                .andExpect(jsonPath("$.series[0].name").value("Gotham"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getTvShowDetail_shouldReturnOk_whenShowExists() throws Exception {
        SerieDetailTMDB show = new SerieDetailTMDB(
                1L,
                "Breaking Bad",
                "Breaking Bad",
                "overview",
                "/poster.jpg",
                "2008-01-20",
                9.5,
                100,
                99.0,
                "/backdrop.jpg",
                5,
                62,
                "Ended",
                List.of()
        );
        when(tmdbService.getTvShowDetailWithTrailer(1L)).thenReturn(show);
        mockMvc.perform(get("/api/tv/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Breaking Bad"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getTvShowDetail_shouldReturnNotFound_whenShowDoesNotExist() throws Exception {
        when(tmdbService.getTvShowDetailWithTrailer(1L)).thenReturn(null);
        mockMvc.perform(get("/api/tv/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getSeasonDetail_shouldReturnOk_whenSeasonExists() throws Exception {
        SeasonSerieDetailTMDB season = new SeasonSerieDetailTMDB(
                1,
                "2024-01-01",
                "Season 1",
                "8",
                List.of(new EpisodeSerieTMDB(1, "Episode 1", "overview", "2024-01-01")),
                "/poster.jpg",
                "season overview"
        );

        when(tmdbService.getSeasonDetail(1L, 1)).thenReturn(season);
        mockMvc.perform(get("/api/tv/1/season/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.season_number").value(1))
                .andExpect(jsonPath("$.name").value("Season 1"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getSeasonDetail_shouldReturnNotFound_whenSeasonDoesNotExist() throws Exception {
        when(tmdbService.getSeasonDetail(1L, 1)).thenReturn(null);
        mockMvc.perform(get("/api/tv/1/season/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getEpisodeDetail_shouldReturnOk_whenEpisodeExists() throws Exception {
        EpisodeSerieDetailTMDB episode = new EpisodeSerieDetailTMDB(
                1,
                "Episode 1",
                "overview",
                "2024-01-01",
                "/still.jpg",
                "Breaking Bad"
        );

        when(tmdbService.getEpisodeDetail(1L, 1, 1)).thenReturn(episode);
        mockMvc.perform(get("/api/tv/1/season/1/episode/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.episode_number").value(1))
                .andExpect(jsonPath("$.name").value("Episode 1"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getEpisodeDetail_shouldReturnNotFound_whenEpisodeDoesNotExist() throws Exception {
        when(tmdbService.getEpisodeDetail(1L, 1, 1)).thenReturn(null);
        mockMvc.perform(get("/api/tv/1/season/1/episode/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getMovieVideos_shouldReturnOk() throws Exception {
        when(tmdbService.getMovieVideos(1L)).thenReturn(List.of(
                new VideoTMDB("1", "en", "Trailer", "YouTube", "1080", "Trailer", "abc123")
        ));
        mockMvc.perform(get("/api/movies/1/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("abc123"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getTvShowVideos_shouldReturnOk() throws Exception {
        when(tmdbService.getTvShowVideos(1L)).thenReturn(List.of(
                new VideoTMDB("1", "en", "Trailer", "YouTube", "1080", "Trailer", "xyz789")
        ));
        mockMvc.perform(get("/api/tv/1/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("xyz789"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getGenreDetail_shouldReturnOk_whenGenreExists() throws Exception {
        User user = mockUser();
        GenreDetailMoviesTMDB detail = new GenreDetailMoviesTMDB(
                12L,
                "Action",
                1,
                1,
                1,
                List.of(new MovieTMDB(1L, "Batman", "2024-01-01", "overview", "/poster.jpg", 8.1))
        );

        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getGenreDetail(12L, 1, 20, user)).thenReturn(detail);
        mockMvc.perform(get("/api/movies/category/12").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Action"))
                .andExpect(jsonPath("$.results[0].title").value("Batman"));
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getGenreDetail_shouldReturnNotFound_whenGenreDoesNotExist() throws Exception {
        User user = mockUser();
        when(userService.getCurrentUser()).thenReturn(user);
        when(tmdbService.getGenreDetail(12L, 1, 20, user)).thenReturn(null);
        mockMvc.perform(get("/api/movies/category/12").param("page", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getAllGenres_shouldReturnOk() throws Exception {
        when(tmdbService.getAllGenres()).thenReturn(List.of());
        mockMvc.perform(get("/api/movies/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "artur2", roles = "USER")
    void getAllTVGenres_shouldReturnOk() throws Exception {
        when(tmdbService.getAllTVGenres()).thenReturn(List.of());
        mockMvc.perform(get("/api/tv/categories"))
                .andExpect(status().isOk());
    }
}