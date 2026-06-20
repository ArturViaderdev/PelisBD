package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.exception.IncorrectTimeWidow;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.TMDBServiceImpl;
import com.arturviader.pelisbdapi.service.UserEpisodeProgressService;
import com.arturviader.pelisbdapi.service.UserMediaService;
import com.arturviader.pelisbdapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMDBServiceImplTests {

    @InjectMocks
    private TMDBServiceImpl tmdbService;

    @Mock
    private UserMediaService userMediaService;

    @Mock
    private UserService userService;

    @Mock
    private UserEpisodeProgressService userEpisodeProgressService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tmdbService, "apiKey", "test-key");
        ReflectionTestUtils.setField(tmdbService, "restTemplate", restTemplate);
    }

    @Test
    void getFirstTrailerKey_shouldReturnSpanishTrailerFirst() {
        List<VideoTMDB> videos = List.of(
                new VideoTMDB("1", "es", "Tráiler oficial en Español", "YouTube", "1080", "Trailer", "key1"),
                new VideoTMDB("2", "es", "Trailer normal", "YouTube", "1080", "Trailer", "key2")
        );

        assertEquals("key1", tmdbService.getFirstTrailerKey(videos));
    }

    @Test
    void getFirstTrailerKey_shouldReturnAnyTrailerIfNoSpanish() {
        List<VideoTMDB> videos = List.of(
                new VideoTMDB("1", "en", "Official Trailer", "YouTube", "1080", "Trailer", "key2")
        );

        assertEquals("key2", tmdbService.getFirstTrailerKey(videos));
    }

    @Test
    void getFirstTrailerKey_shouldReturnNullWhenNoTrailer() {
        assertNull(tmdbService.getFirstTrailerKey(List.of()));
    }

    @Test
    void getMovieById_shouldReturnBody() {
        MovieDetailTMDB movie = new MovieDetailTMDB(1L, "Batman", "2024-01-01", "overview", "/poster", 8.1, null, null, List.of(), List.of(), null, 10);
        when(restTemplate.getForEntity(anyString(), eq(MovieDetailTMDB.class))).thenReturn(ResponseEntity.ok(movie));
        MovieDetailTMDB result = tmdbService.getMovieById(1L);
        assertNotNull(result);
        assertEquals("Batman", result.getTitle());
        verify(restTemplate).getForEntity(contains("/movie/1"), eq(MovieDetailTMDB.class));
    }

    @Test
    void getPopularMovies_shouldAddWatchStatus() {
        User user = new User();
        user.setUserName("artur2");
        MovieTMDB movie = new MovieTMDB(1L, "Batman", "2024-01-01", "overview", "/poster", 8.1);
        MoviesResponseTMDB response = new MoviesResponseTMDB(1, 1, 1, List.of(movie));
        when(restTemplate.getForEntity(anyString(), eq(MoviesResponseTMDB.class))).thenReturn(ResponseEntity.ok(response));
        when(userMediaService.isMovieWatched(1L, "artur2", MediaType.movie)).thenReturn(true);
        when(userMediaService.isMovieInWatchlist("artur2", 1L, MediaType.movie)).thenReturn(false);
        MoviesResponseTMDB result = tmdbService.getPopularMovies(1, user);
        assertNotNull(result);
        assertTrue(result.results().get(0).isWatched());
        assertFalse(result.results().get(0).isWatchListed());
    }

    @Test
    void getTrendingMovies_shouldThrowOnInvalidWindow() {
        assertThrows(IncorrectTimeWidow.class, () -> tmdbService.getTrendingMovies("month", 1, null));
    }

    @Test
    void getTrendingTvShows_shouldReturnNullOnException() {
        when(restTemplate.getForEntity(anyString(), eq(SeriesResponseTMDB.class))).thenThrow(new RuntimeException("fail"));

        SeriesResponseTMDB result = tmdbService.getTrendingTvShows("day", 1, null);

        assertNull(result);
    }

    @Test
    void searchMovie_shouldReturnBody() {
        MoviesResponseTMDB response = new MoviesResponseTMDB(1, 1, 1, List.of());
        when(restTemplate.getForEntity(anyString(), eq(MoviesResponseTMDB.class))).thenReturn(ResponseEntity.ok(response));
        MoviesResponseTMDB result = tmdbService.searchMovie("batman", 1, null);
        assertNotNull(result);
    }

    @Test
    void getTvShowDetailWithTrailer_shouldAttachTrailerAndVideos() {
        SerieDetailTMDB detail = new SerieDetailTMDB(
                1L, "Breaking Bad", "Breaking Bad", "overview", "/poster",
                "2008-01-01", 9.5, 100, 99.0, null, 5, 60, "Ended", List.of()
        );

        List<VideoTMDB> videos = List.of(
                new VideoTMDB("1", "es", "Tráiler oficial en Español", "YouTube", "1080", "Trailer", "k1")
        );

        VideosResponseDTO videosResponse = new VideosResponseDTO("video-id-1", videos);
        when(restTemplate.getForEntity(anyString(), eq(SerieDetailTMDB.class)))
                .thenReturn(ResponseEntity.ok(detail));
        when(restTemplate.getForEntity(contains("/videos"), eq(VideosResponseDTO.class)))
                .thenReturn(ResponseEntity.ok(videosResponse));
        SerieDetailTMDB result = tmdbService.getTvShowDetailWithTrailer(1L);
        assertNotNull(result);
        assertEquals("k1", result.getTrailerKey());
        assertEquals(1, result.getVideos().size());
    }

    @Test
    void getSeasonDetail_shouldSetTvNameAndWatchedFlags() {
        User user = new User();
        user.setId(10L);
        user.setUserName("artur2");
        SeasonSerieDetailTMDB season = new SeasonSerieDetailTMDB(1, "2024-01-01", "Season 1", "8",
                List.of(new EpisodeSerieTMDB(1, "Ep1", "overview", "2024-01-01")),
                "/poster", "overview");
        when(restTemplate.getForEntity(anyString(), eq(SerieDetailTMDB.class)))
                .thenReturn(ResponseEntity.ok(new SerieDetailTMDB(1L, "Breaking Bad", "Breaking Bad", "overview", "/poster", "2008-01-01", 9.5, 100, 99.0, null, 5, 60, "Ended", List.of())));
        when(restTemplate.getForEntity(contains("/season/1"), eq(SeasonSerieDetailTMDB.class)))
                .thenReturn(ResponseEntity.ok(season));
        when(userService.getCurrentUser()).thenReturn(user);
        when(userEpisodeProgressService.getEpisodeWatched(10L, 1L, 1, 1)).thenReturn(true);
        SeasonSerieDetailTMDB result = tmdbService.getSeasonDetail(1L, 1);
        assertNotNull(result);
        assertEquals("Breaking Bad", result.getTvName());
        assertTrue(result.getEpisodes().get(0).isWatched());
    }
}