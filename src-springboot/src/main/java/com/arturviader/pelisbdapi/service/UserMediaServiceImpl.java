package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.UserWatchListMapper;
import com.arturviader.pelisbdapi.dto.UserWatchedMapper;
import com.arturviader.pelisbdapi.dto.WatchedItemDto;
import com.arturviader.pelisbdapi.dto.WatchlistItemDto;
import com.arturviader.pelisbdapi.exception.ResourceNotFoundException;
import com.arturviader.pelisbdapi.model.*;
import com.arturviader.pelisbdapi.repository.UserEpisodeProgressRepository;
import com.arturviader.pelisbdapi.repository.UserRepository;
import com.arturviader.pelisbdapi.repository.UserWatchedItemRepository;
import com.arturviader.pelisbdapi.repository.UserWatchlistItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserMediaServiceImpl implements UserMediaService {

    @Autowired
    private UserWatchedItemRepository watchedRepo;

    @Autowired
    private UserWatchlistItemRepository watchlistRepository;

    @Autowired
    private UserEpisodeProgressRepository episodeRepo;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TMDBService tmdbService;

    @Autowired
    private UserWatchedMapper userWatchedMapper;

    @Autowired
    private UserWatchListMapper userWatchListMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addToWatched(User user, MediaType type, Long itemId) {
        System.out.println(user.getUserName());
        System.out.println(user.getEmail());
        UserWatchedItem item = watchedRepo.findByUserAndTypeAndItemId(user, type, itemId)
                .orElse(new UserWatchedItem());

        movieService.saveIfNotExists(tmdbService.getMovieById(itemId),"movie");
        item.setUser(user);
        item.setType(type);
        item.setItemId(itemId);
        item.setWatchedAt(LocalDateTime.now());
        watchedRepo.save(item);
    }

    @Override
    public void removeFromWatched(User user, MediaType type, Long itemId) {
        UserWatchedItem item = watchedRepo.findByUserAndTypeAndItemId(user, type, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        watchedRepo.delete(item);
    }

    @Override
    public List<WatchedItemDto> getWatchedList(User user) {
        return watchedRepo.findByUser(user).stream()
                .map(userWatchedMapper::toDtoWithMovieData) // ✅ Ahora funciona
                .collect(Collectors.toList());
    }

    @Override
    public boolean isMovieWatched(Long tmdbId, String userName) {
        Optional<User> userOpt = userRepository.findByUserName(userName);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        return watchedRepo.existsByUserAndItemIdAndType(user, tmdbId, MediaType.movie);
    }

    @Override
    public void addToWatchlist(User user, MediaType type, Long tmdbId) {
        UserWatchlistItem item = watchlistRepository.findByUserAndTypeAndItemId(user, type, tmdbId)
                .orElse(new UserWatchlistItem());
        movieService.saveIfNotExists(tmdbService.getMovieById(tmdbId),"movie");
        item.setUser(user);
        item.setType(type);
        item.setItemId(tmdbId);

        watchlistRepository.save(item);
    }

    @Override
    public void removeFromWatchlist(User user, MediaType type, Long tmdbId) {
        UserWatchlistItem item = watchlistRepository.findByUserAndTypeAndItemId(user, type, tmdbId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        watchlistRepository.delete(item);
    }

    @Override
    public boolean isMovieInWatchlist(String userId, Long tmdbId) {
        Optional<User> userOpt = userRepository.findByUserName(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return watchlistRepository.existsByUserAndItemIdAndType(user, tmdbId, MediaType.movie);
    }

    @Override
    public List<WatchlistItemDto> getWatchlist(User user) {
        return watchlistRepository.findByUser(user).stream()
                .map(userWatchListMapper::toDtoWithMovieData) // ✅ Ahora funciona
                .collect(Collectors.toList());
    }


}