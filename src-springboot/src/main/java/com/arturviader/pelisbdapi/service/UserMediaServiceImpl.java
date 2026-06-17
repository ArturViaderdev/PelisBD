package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private TvService tvService;

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
        if(type.equals(MediaType.movie))
        {
            movieService.saveIfNotExists(tmdbService.getMovieById(itemId),"movie");
        }
        else
        {
             tvService.saveIfNotExists(tmdbService.getTvShowDetail(itemId),"tv");
        }
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
    public boolean isMovieWatched(Long tmdbId, String userName, MediaType mediaType) {
        Optional<User> userOpt = userRepository.findByUserName(userName);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        return watchedRepo.existsByUserAndItemIdAndType(user, tmdbId, mediaType);
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
    public boolean isMovieInWatchlist(String userId, Long tmdbId,MediaType mediaType) {
        Optional<User> userOpt = userRepository.findByUserName(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return watchlistRepository.existsByUserAndItemIdAndType(user, tmdbId, mediaType);
    }

    @Override
    public WatchListResponse getWatchlist(User user, int page, int size, String sort) {
        String userId = user.getUserName();
        List<UserWatchlistItem> watchListItems = watchlistRepository.findByUser(user);
        int totalElements = watchListItems.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = false;
        boolean last = false;
        if(page<=1)
        {
            first=true;
        }
        if(page>=totalPages)
        {
            last = true;
        }
        int initialResult =(page-1)*size;
        int endResult = initialResult + size;
        if(endResult>totalElements)
        {
            endResult = totalElements-1;
        }
        List<UserWatchlistItem> results = new ArrayList<>();
        for(int count=initialResult; count<=endResult;count++)
        {
            results.add(watchListItems.get(count));
        }
        List<WatchlistItemDto> itemsdto = results.stream()
                .map(item -> {
                    boolean isWatched = isMovieWatched(item.getItemId(),userId, item.getType());
                    return userWatchListMapper.toDtoWithMovieData(item, isWatched);
                })
                .toList();
        return new WatchListResponse(itemsdto,page,size,totalElements,totalPages,last,first);
    }

    @Override
    public WatchedResponse getWatchedList(User user, int page, int size, String sort) {
        String userId = user.getUserName();
        List<UserWatchedItem> watchedItems = watchedRepo.findByUser(user);
        int totalElements = watchedItems.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = false;
        boolean last = false;
        if(page<=1)
        {
            first=true;
        }
        if(page>=totalPages)
        {
            last = true;
        }
        int initialResult =(page-1)*size;
        int endResult = initialResult + size;
        if(endResult>totalElements)
        {
            endResult = totalElements-1;
        }
        List<UserWatchedItem> results = new ArrayList<>();
        for(int count=initialResult; count<=endResult;count++)
        {
            results.add(watchedItems.get(count));
        }
        List<WatchedItemDto> itemsdto = results.stream()
                .map(item -> {
                    boolean isWatchListed = isMovieInWatchlist(userId, item.getItemId(), item.getType());
                    return userWatchedMapper.toDtoWithMovieData(item, isWatchListed);
                })
                .toList();
        return new WatchedResponse(itemsdto,page,size,totalElements,totalPages,last,first);
    }
}