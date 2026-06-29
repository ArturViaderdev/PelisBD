package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.UserEpisodeProgressService;
import com.arturviader.pelisbdapi.service.UserMediaService;
import com.arturviader.pelisbdapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserMediaController {

    @Autowired
    private UserMediaService mediaService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEpisodeProgressService episodeProgressService;

    @PostMapping("/api/user/watched")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> addToWatched(@RequestBody WatchedRequest request) {
        User user = getCurrentUser();
        mediaService.addToWatched(user, MediaType.valueOf(request.type()), request.itemId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/user/watched/{type}/{itemId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> removeFromWatched(@PathVariable String type, @PathVariable Long itemId) {
        User user = getCurrentUser();
        mediaService.removeFromWatched(user, MediaType.valueOf(type), itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/watched/status/{type}/{tmdbId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<BooleanDto> isMovieWatched(
            @PathVariable MediaType type,
            @PathVariable Long tmdbId) {
        User user = getCurrentUser();
        boolean isWatched = mediaService.isMovieWatched(tmdbId, user.getUserName(), type);
        return ResponseEntity.ok(new BooleanDto(isWatched));
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/api/user/watchlist")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<WatchListResponse> getWatchlist(@RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "size", defaultValue = "12") int size
            , @RequestParam(name = "sort", defaultValue = "dateadd") String sort) {
        User user = getCurrentUser();
        WatchListResponse list = mediaService.getWatchlist(user, page, size, sort);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/user/watched")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<WatchedResponse> getWatchedList(@RequestParam(name = "page", defaultValue = "1") int page
            , @RequestParam(name = "size", defaultValue = "12") int size
            , @RequestParam(name = "sort", defaultValue = "dateadd") String sort) {
        User user = getCurrentUser();
        WatchedResponse list = mediaService.getWatchedList(user, page, size, sort);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/watched")
    public ResponseEntity<WatchedResponse> getGlobalWatchedList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "sort", defaultValue = "dateadd") String sort) {
        WatchedResponse list = mediaService.getGlobalWatchedList(page, size, sort);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/api/user/watchlist")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<String> addToWatchlist(@RequestBody WatchListRequest request) {
        User user = getCurrentUser();
        mediaService.addToWatchlist(user, MediaType.valueOf(request.type()), request.itemId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/user/watchlist/{type}/{itemId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<String> removeFromWatchlist(@PathVariable String type, @PathVariable Long itemId) {
        User user = getCurrentUser();
        mediaService.removeFromWatchlist(user, MediaType.valueOf(type), itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/watchlist/status/{type}/{tmbdId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<BooleanDto> isMovieInWatchlist(@PathVariable MediaType type, @PathVariable Long tmbdId) {
        User user = getCurrentUser();
        boolean isInWatchList = mediaService.isMovieInWatchlist(user.getUserName(), tmbdId, type);
        return ResponseEntity.ok(new BooleanDto(isInWatchList));
    }

    @PostMapping("/api/user/tv/{tvId}/episode")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public UserEpisodeProgressDto markEpisode(
            @PathVariable Long tvId,
            @RequestBody MarkEpisodeRequest request) {
        return episodeProgressService.markEpisode(
                tvId, request.season(), request.episode(), request.watched());
    }

    @GetMapping("/api/user/tv/{tvId}/season/{seasonNumber}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public List<UserEpisodeProgressDto> getSeasonProgress(
            @PathVariable Long tvId,
            @PathVariable Integer seasonNumber) {
        return episodeProgressService.getSeasonProgress(tvId, seasonNumber);
    }
}