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

    @Autowired
    private ReviewService reviewService;

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
        if(type.equals(MediaType.movie))
        {
            movieService.saveIfNotExists(tmdbService.getMovieById(tmdbId),"movie");
        }
        else
        {
            tvService.saveIfNotExists(tmdbService.getTvShowDetail(tmdbId),"tv");
        }
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

    /*private void sortWatchlistItems(List<UserWatchlistItem> items, String sort, User user) {
        for(int i=0; i < items.size()-1; i++){
            for(int j=0; j < (items.size()-1-i); j++){
                if(sort.equals("dateadd"))
                {
                    if(items.get(i).getAddedAt().isBefore(items.get(j+1).getAddedAt())){
                        UserWatchlistItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("ownrating"))
                {
                    if(reviewService.getUserRate(user.getUserName(),items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getUserRate(user.getUserName(),items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchlistItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("userating"))
                {
                    if(reviewService.getAverageRating(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getAverageRating(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchlistItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("numrating")){
                    if(reviewService.getTotalRatings(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getTotalRatings(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchlistItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("rating"))
                {
                    if(reviewService.getTotalRatings(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getTotalRatings(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchlistItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
            }
        }
    }*/

    private String getTitleFromMedia(Long itemId, MediaType type) {
        return switch (type) {
            case movie -> movieService.findById(itemId).get().getTitle();
            case tv -> tvService.findById(itemId).get().getTitle();
            default -> null;
        };
    }

    private void sortWatchedItems(List<UserWatchedItem> items, String sort, User user) {
        items.sort((a, b) -> {
            return switch (sort) {
                case "dateadd" -> a.getWatchedAt().compareTo(b.getWatchedAt());
                case "ownrating" -> {
                    int ratingA = reviewService.getUserRate(user.getUserName(), a.getItemId(), a.getType().toString());
                    int ratingB = reviewService.getUserRate(user.getUserName(), b.getItemId(), b.getType().toString());
                    yield Integer.compare(ratingB, ratingA); // Descendente
                }
                case "userating" -> {
                    Double avgRatingA = reviewService.getAverageRating(a.getItemId(), a.getType().toString());
                    Double avgRatingB = reviewService.getAverageRating(b.getItemId(), b.getType().toString());
                    if (avgRatingA == null) avgRatingA = 0.0;
                    if (avgRatingB == null) avgRatingB = 0.0;
                    yield avgRatingB.compareTo(avgRatingA);
                }
                case "numrating" -> {
                    Long countA = reviewService.getTotalRatings(a.getItemId(), a.getType().toString());
                    Long countB = reviewService.getTotalRatings(b.getItemId(), b.getType().toString());
                    if (countA == null) countA = 0L;
                    if (countB == null) countB = 0L;
                    yield countB.compareTo(countA);
                }
                case "title" -> {
                    String titleA = getTitleFromMedia(a.getItemId(), a.getType());
                    String titleB = getTitleFromMedia(b.getItemId(), b.getType());
                    if (titleA == null) titleA = "";
                    if (titleB == null) titleB = "";
                    yield titleA.compareToIgnoreCase(titleB);
                }
                default -> 0;
            };
        });
    }


    /*private void sortWatchedItems(List<UserWatchedItem> items, String sort, User user) {
        for(int i=0; i < items.size()-1; i++){
            for(int j=0; j < (items.size()-1-i); j++){
                if(sort.equals("dateadd"))
                {
                    if(items.get(i).getWatchedAt().isBefore(items.get(j+1).getWatchedAt())){
                        UserWatchedItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("ownrating"))
                {
                    if(reviewService.getUserRate(user.getUserName(),items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getUserRate(user.getUserName(),items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchedItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("userating"))
                {
                    if(reviewService.getAverageRating(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getAverageRating(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchedItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("numrating")){
                    if(reviewService.getTotalRatings(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getTotalRatings(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchedItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
                else if(sort.equals("rating"))
                {
                    if(reviewService.getTotalRatings(items.get(i).getItemId(),items.get(i).getType().toString()) < reviewService.getTotalRatings(items.get(j+1).getItemId(),items.get(j+1).getType().toString())){
                        UserWatchedItem aux=items.get(j);
                        items.set(j,items.get(j+1));
                        items.set(j+1,aux);
                    }
                }
            }
        }

    }*/

    private void sortWatchlistItems(List<UserWatchlistItem> items, String sort, User user) {
        items.sort((a, b) -> {
            return switch (sort) {
                case "dateadd" -> a.getAddedAt().compareTo(b.getAddedAt());
                case "ownrating" -> {
                    int ratingA = reviewService.getUserRate(user.getUserName(), a.getItemId(), a.getType().toString());
                    int ratingB = reviewService.getUserRate(user.getUserName(), b.getItemId(), b.getType().toString());
                    yield Integer.compare(ratingB, ratingA); // Descendente: mayor primero
                }
                case "userating" -> {
                    Double avgRatingA = reviewService.getAverageRating(a.getItemId(), a.getType().toString());
                    Double avgRatingB = reviewService.getAverageRating(b.getItemId(), b.getType().toString());
                    if (avgRatingA == null) avgRatingA = 0.0;
                    if (avgRatingB == null) avgRatingB = 0.0;
                    yield avgRatingB.compareTo(avgRatingA);
                }
                case "numrating" -> {
                    Long countA = reviewService.getTotalRatings(a.getItemId(), a.getType().toString());
                    Long countB = reviewService.getTotalRatings(b.getItemId(), b.getType().toString());
                    if (countA == null) countA = 0L;
                    if (countB == null) countB = 0L;
                    yield countB.compareTo(countA);
                }
                case "title" -> {
                    String titleA = getTitleFromMedia(a.getItemId(), a.getType());
                    String titleB = getTitleFromMedia(b.getItemId(), b.getType());
                    if (titleA == null) titleA = "";
                    if (titleB == null) titleB = "";
                    yield titleA.compareToIgnoreCase(titleB); // Ascendente
                }
                default -> 0;
            };
        });
    }


    @Override
    public WatchListResponse getWatchlist(User user, int page, int size, String sort) {
        String userId = user.getUserName();
        List<UserWatchlistItem> watchListItems = watchlistRepository.findByUser(user);
        sortWatchlistItems(watchListItems,sort, user);
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
        sortWatchedItems(watchedItems,sort, user);
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