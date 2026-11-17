package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.exception.ResourceNotFoundException;
import com.arturviader.pelisbdapi.mapper.UserWatchListMapper;
import com.arturviader.pelisbdapi.mapper.UserWatchedMapper;
import com.arturviader.pelisbdapi.model.*;
import com.arturviader.pelisbdapi.repository.UserEpisodeProgressRepository;
import com.arturviader.pelisbdapi.repository.UserRepository;
import com.arturviader.pelisbdapi.repository.UserWatchedItemRepository;
import com.arturviader.pelisbdapi.repository.UserWatchlistItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private ReviewServiceImpl reviewService;

    @Override
    public void addToWatched(User user, MediaType type, Long itemId) {
        UserWatchedItem item = watchedRepo.findByUserAndTypeAndItemId(user, type, itemId)
                .orElse(new UserWatchedItem());
        if (type.equals(MediaType.movie)) {
            movieService.saveIfNotExists(tmdbService.getMovieById(itemId), "movie");
        } else {
            tvService.saveIfNotExists(tmdbService.getTvShowDetail(itemId), "tv");
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
        if (type.equals(MediaType.movie)) {
            movieService.saveIfNotExists(tmdbService.getMovieById(tmdbId), "movie");
        } else {
            tvService.saveIfNotExists(tmdbService.getTvShowDetail(tmdbId), "tv");
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
    public boolean isMovieInWatchlist(String userId, Long tmdbId, MediaType mediaType) {
        Optional<User> userOpt = userRepository.findByUserName(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return watchlistRepository.existsByUserAndItemIdAndType(user, tmdbId, mediaType);
    }

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
        Long watchlistUserId = user.getId();
        String reviewUsername = user.getUserName();

        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int pageIndex = safePage - 1;

        Page<UserWatchlistItem> watchlistPage = switch (sort == null ? "dateadd" : sort.toLowerCase()) {
            case "dateadd" -> watchlistRepository.findByUser(
                    user,
                    PageRequest.of(pageIndex, safeSize, Sort.by(Sort.Direction.DESC, "addedAt"))
            );

            case "ownrating" -> watchlistRepository.findByUserOrderByOwnRatingDesc(
                    watchlistUserId,
                    reviewUsername,
                    PageRequest.of(pageIndex, safeSize)
            );

            case "userating" -> watchlistRepository.findByUserOrderByAverageRatingDesc(
                    watchlistUserId,
                    PageRequest.of(pageIndex, safeSize)
            );

            case "numrating" -> watchlistRepository.findByUserOrderByRatingsCountDesc(
                    watchlistUserId,
                    PageRequest.of(pageIndex, safeSize)
            );

            default -> watchlistRepository.findByUser(
                    user,
                    PageRequest.of(pageIndex, safeSize, Sort.by(Sort.Direction.ASC, "addedAt"))
            );
        };

        List<WatchlistItemDto> itemsdto = watchlistPage.getContent().stream()
                .map(item -> {
                    boolean isWatched = isMovieWatched(item.getItemId(), reviewUsername, item.getType());
                    return userWatchListMapper.toDtoWithMovieData(item, isWatched);
                })
                .toList();

        return new WatchListResponse(
                itemsdto,
                safePage,
                safeSize,
                (int) watchlistPage.getTotalElements(),
                watchlistPage.getTotalPages(),
                watchlistPage.isLast(),
                watchlistPage.isFirst()
        );
    }

    @Override
    public WatchedResponse getWatchedList(User user, int page, int size, String sort) {
        Long watchedUserId = user.getId();
        String reviewUsername = user.getUserName();

        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int pageIndex = safePage - 1;

        Page<UserWatchedItem> watchedPage = switch (sort == null ? "dateadd" : sort.toLowerCase()) {
            case "dateadd" -> watchedRepo.findByUser(
                    user,
                    PageRequest.of(pageIndex, safeSize, Sort.by(Sort.Direction.DESC, "watchedAt"))
            );

            case "ownrating" -> watchedRepo.findByUserOrderByOwnRatingDesc(
                    watchedUserId,
                    reviewUsername,
                    PageRequest.of(pageIndex, safeSize)
            );

            case "userating" -> watchedRepo.findByUserOrderByAverageRatingDesc(
                    watchedUserId,
                    PageRequest.of(pageIndex, safeSize)
            );

            case "numrating" -> watchedRepo.findByUserOrderByRatingsCountDesc(
                    watchedUserId,
                    PageRequest.of(pageIndex, safeSize)
            );

            default -> watchedRepo.findByUser(
                    user,
                    PageRequest.of(pageIndex, safeSize, Sort.by(Sort.Direction.ASC, "watchedAt"))
            );
        };

        List<WatchedItemDto> itemsdto = watchedPage.getContent().stream()
                .map(item -> {
                    boolean isWatchListed = isMovieInWatchlist(user.getUserName(), item.getItemId(), item.getType());
                    return userWatchedMapper.toDtoWithMovieData(item, isWatchListed);
                })
                .toList();

        return new WatchedResponse(
                itemsdto,
                safePage,
                safeSize,
                (int) watchedPage.getTotalElements(),
                watchedPage.getTotalPages(),
                watchedPage.isLast(),
                watchedPage.isFirst()
        );
    }

    @Override
    public WatchedResponse getGlobalWatchedList(int page, int size, String sort, User user) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int pageIndex = safePage - 1;

        Page<GlobalWatchedProjection> watchedPage = switch (sort == null ? "dateadd" : sort.toLowerCase()) {
            case "dateadd" -> watchedRepo.findGlobalWatchedDistinctOrderByDateDesc(
                    PageRequest.of(pageIndex, safeSize)
            );

            case "userating" -> watchedRepo.findGlobalWatchedDistinctOrderByAverageRatingDesc(
                    PageRequest.of(pageIndex, safeSize)
            );

            case "numrating" -> watchedRepo.findGlobalWatchedDistinctOrderByRatingsCountDesc(
                    PageRequest.of(pageIndex, safeSize)
            );

            default -> watchedRepo.findGlobalWatchedDistinctOrderByTitleAsc(
                    PageRequest.of(pageIndex, safeSize)
            );
        };

        List<GlobalWatchedProjection> pageContent = watchedPage.getContent();

        // Flags del usuario: se calculan en bloque (2 consultas por página).
        // Si no hay usuario autenticado NO se consulta nada y todo queda a false.
        Set<String> userWatchedKeys = Collections.emptySet();
        Set<String> userWatchlistKeys = Collections.emptySet();

        if (user != null && !pageContent.isEmpty()) {
            Set<Long> itemIds = pageContent.stream()
                    .map(GlobalWatchedProjection::getItemId)
                    .collect(Collectors.toSet());

            userWatchedKeys = watchedRepo.findByUserIdAndItemIdIn(user.getId(), itemIds).stream()
                    .map(w -> mediaKey(w.getType(), w.getItemId()))
                    .collect(Collectors.toSet());

            userWatchlistKeys = watchlistRepository.findByUserIdAndItemIdIn(user.getId(), itemIds).stream()
                    .map(w -> mediaKey(w.getType(), w.getItemId()))
                    .collect(Collectors.toSet());
        }

        final Set<String> watchedKeys = userWatchedKeys;
        final Set<String> watchlistKeys = userWatchlistKeys;

        List<WatchedItemDto> itemsdto = pageContent.stream()
                .map(item -> {
                    MediaType mediaType = MediaType.valueOf(item.getType().trim());
                    String key = mediaKey(mediaType, item.getItemId());

                    boolean isWatchListed = watchlistKeys.contains(key);
                    boolean isWatched = watchedKeys.contains(key);

                    GlobalWatchedItem globalItem = new GlobalWatchedItem(
                            item.getItemId(),
                            mediaType,
                            item.getWatchedAt()
                    );

                    return userWatchedMapper.toGlobalDto(globalItem, isWatchListed, isWatched);
                })
                .toList();

        return new WatchedResponse(
                itemsdto,
                safePage,
                safeSize,
                (int) watchedPage.getTotalElements(),
                watchedPage.getTotalPages(),
                watchedPage.isLast(),
                watchedPage.isFirst()
        );
    }

    /** Clave única tipo+id (una película y una serie pueden compartir id de TMDB). */
    private static String mediaKey(MediaType type, Long itemId) {
        return type.name() + ":" + itemId;
    }

    private void sortGlobalProjectedItems(List<GlobalWatchedItem> items, String sort) {
        items.sort((a, b) -> switch (sort) {
            case "dateadd" -> {
                LocalDateTime dateA = a.watchedAt() != null ? a.watchedAt() : LocalDateTime.MIN;
                LocalDateTime dateB = b.watchedAt() != null ? b.watchedAt() : LocalDateTime.MIN;
                yield dateB.compareTo(dateA); // más reciente primero
            }
            case "userating" -> {
                Double avgRatingA = reviewService.getAverageRating(a.itemId(), a.type().toString());
                Double avgRatingB = reviewService.getAverageRating(b.itemId(), b.type().toString());
                if (avgRatingA == null) avgRatingA = 0.0;
                if (avgRatingB == null) avgRatingB = 0.0;
                yield avgRatingB.compareTo(avgRatingA);
            }
            case "numrating" -> {
                Long countA = reviewService.getTotalRatings(a.itemId(), a.type().toString());
                Long countB = reviewService.getTotalRatings(b.itemId(), b.type().toString());
                if (countA == null) countA = 0L;
                if (countB == null) countB = 0L;
                yield countB.compareTo(countA);
            }
            case "title" -> {
                String titleA = getTitleFromMedia(a.itemId(), a.type());
                String titleB = getTitleFromMedia(b.itemId(), b.type());
                if (titleA == null) titleA = "";
                if (titleB == null) titleB = "";
                yield titleA.compareToIgnoreCase(titleB);
            }
            default -> 0;
        });
    }

}