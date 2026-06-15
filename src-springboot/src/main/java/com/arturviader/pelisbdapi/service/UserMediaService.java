package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.UserWatchedMapper;
import com.arturviader.pelisbdapi.dto.WatchedItemDto;
import com.arturviader.pelisbdapi.exception.ResourceNotFoundException;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import com.arturviader.pelisbdapi.repository.UserEpisodeProgressRepository;
import com.arturviader.pelisbdapi.repository.UserWatchedItemRepository;
import com.arturviader.pelisbdapi.repository.UserWatchlistItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public interface UserMediaService{
    void addToWatched(User user, MediaType type, Long itemId);
    void removeFromWatched(User user, MediaType type, Long itemId);
    List<WatchedItemDto> getWatchedList(User user);
    boolean isMovieWatched(Long id, String username);
}

