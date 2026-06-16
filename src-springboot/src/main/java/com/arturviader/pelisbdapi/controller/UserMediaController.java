package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.UserMediaService;
import com.arturviader.pelisbdapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserMediaController {

    @Autowired
    private UserMediaService mediaService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/watched")
    public ResponseEntity<Void> addToWatched(@RequestBody WatchedRequest request) {
        User user = getCurrentUser();
        mediaService.addToWatched(user, MediaType.valueOf(request.type()), request.itemId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/user/watched/{type}/{itemId}")
    public ResponseEntity<Void> removeFromWatched(@PathVariable String type, @PathVariable Long itemId) {
        User user = getCurrentUser();
        mediaService.removeFromWatched(user, MediaType.valueOf(type), itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/watched")
    public ResponseEntity<List<WatchedItemDto>> getWatchedList() {
        User user = getCurrentUser();
        List<WatchedItemDto> list = mediaService.getWatchedList(user);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/user/watched/{tmdbId}")
    public ResponseEntity<BooleanDto> isMovieWatched(
            @PathVariable Long tmdbId)
    {
        User user = getCurrentUser();
        boolean isWatched = mediaService.isMovieWatched(tmdbId, user.getUserName(),MediaType.movie);
        return ResponseEntity.ok(new BooleanDto(isWatched));
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/api/user/watchlist")
    public ResponseEntity<List<WatchlistItemDto>> getWatchlist() {
        User user = getCurrentUser();
        List<WatchlistItemDto> list = mediaService.getWatchlist(user);
        return ResponseEntity.ok(list);
    }


    @PostMapping("/api/user/watchlist")
    public ResponseEntity<String> addToWatchlist(@RequestBody WatchListRequest request) {
        User user = getCurrentUser();
        mediaService.addToWatchlist(user,MediaType.valueOf(request.type()),request.itemId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/user/watchlist/{type}/{itemId}")
    public ResponseEntity<String> removeFromWatchlist(@PathVariable String type, @PathVariable Long itemId) {
        User user = getCurrentUser();
        mediaService.removeFromWatchlist(user, MediaType.valueOf(type), itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/watchlist/{tmbdId}")
    public ResponseEntity<BooleanDto> isMovieInWatchlist(@PathVariable Long tmbdId) {
        User user = getCurrentUser();
        boolean isInWatchList = mediaService.isMovieInWatchlist(user.getUserName(),tmbdId,MediaType.movie);
        return ResponseEntity.ok(new BooleanDto(isInWatchList));
    }
}