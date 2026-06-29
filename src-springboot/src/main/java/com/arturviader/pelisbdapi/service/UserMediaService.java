package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;

public interface UserMediaService{
    void addToWatched(User user, MediaType type, Long itemId);
    void removeFromWatched(User user, MediaType type, Long itemId);
    WatchedResponse getWatchedList(User user, int page, int size, String sort);
    WatchedResponse getGlobalWatchedList(int page, int size, String sort);
    boolean isMovieWatched(Long id, String username,MediaType mediaType);
    void addToWatchlist(User user, MediaType type, Long tmdbId);
    void removeFromWatchlist(User user, MediaType type, Long tmdbId);
    boolean isMovieInWatchlist(String userId, Long tmdbId, MediaType mediaType);
    WatchListResponse getWatchlist(User user, int page, int size, String sort);
}

