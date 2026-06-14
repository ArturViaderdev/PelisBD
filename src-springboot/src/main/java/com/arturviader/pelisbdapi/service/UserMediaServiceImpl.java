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

@Service
@Transactional
public class UserMediaServiceImpl implements UserMediaService {

    @Autowired
    private UserWatchedItemRepository watchedRepo;

    @Autowired
    private UserWatchlistItemRepository watchlistRepo;

    @Autowired
    private UserEpisodeProgressRepository episodeRepo;

    public void addToWatched(User user, MediaType type, Long itemId) {
        UserWatchedItem item = watchedRepo.findByUserAndTypeAndItemId(user, type, itemId)
                .orElse(new UserWatchedItem());
        item.setUser(user);
        item.setType(type);
        item.setItemId(itemId);
        item.setWatchedAt(LocalDateTime.now());
        watchedRepo.save(item);
    }

    public void removeFromWatched(User user, MediaType type, Long itemId) {
        UserWatchedItem item = watchedRepo.findByUserAndTypeAndItemId(user, type, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        watchedRepo.delete(item);
    }

    public List<WatchedItemDto> getWatchedList(User user) {
        return watchedRepo.findByUser(user).stream()
                .map(UserWatchedMapper::toDto)
                .collect(Collectors.toList());
    }
}