package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchlistItemRepository extends JpaRepository<UserWatchlistItem, Long> {

    Optional<UserWatchlistItem> findByUserAndTypeAndItemId(User user, MediaType type, Long itemId);

    List<UserWatchlistItem> findByUser(User user);

    List<UserWatchlistItem> findByTypeAndItemId(MediaType type, Long itemId);

    boolean existsByUserAndItemIdAndType(User user, Long itemId, MediaType type);

    Page<UserWatchlistItem> findByUser(User user, Pageable pageable);

    /**
     * Devuelve los items de la lista del usuario cuyo itemId esté en la lista dada.
     * Se usa para resolver en UNA sola consulta qué elementos de una página
     * tiene el usuario en "mi lista".
     */
    @Query("""
        SELECT w FROM UserWatchlistItem w
        WHERE w.user.id = :userId
          AND w.itemId IN :itemIds
    """)
    List<UserWatchlistItem> findByUserIdAndItemIdIn(
            @Param("userId") Long userId,
            @Param("itemIds") java.util.Collection<Long> itemIds
    );

    @Query(
            value = """
            SELECT uwi.*
            FROM user_watchlist_items uwi
            LEFT JOIN user_reviews r
                ON r.tmdb_id = uwi.item_id
               AND LOWER(
                    CASE r.media_type
                        WHEN 0 THEN 'movie'
                        WHEN 1 THEN 'tv'
                        ELSE NULL
                    END
               ) = LOWER(CAST(uwi.type AS text))
               AND r.user_id = :reviewUsername
            WHERE uwi.user_id = :watchlistUserId
            ORDER BY COALESCE(r.rating, 0) DESC, uwi.added_at DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM user_watchlist_items uwi
            WHERE uwi.user_id = :watchlistUserId
        """,
            nativeQuery = true
    )
    Page<UserWatchlistItem> findByUserOrderByOwnRatingDesc(
            @Param("watchlistUserId") Long watchlistUserId,
            @Param("reviewUsername") String reviewUsername,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT uwi.*
            FROM user_watchlist_items uwi
            LEFT JOIN user_reviews r
                ON r.tmdb_id = uwi.item_id
               AND LOWER(
                    CASE r.media_type
                        WHEN 0 THEN 'movie'
                        WHEN 1 THEN 'tv'
                        ELSE NULL
                    END
               ) = LOWER(CAST(uwi.type AS text))
            WHERE uwi.user_id = :watchlistUserId
            GROUP BY uwi.id
            ORDER BY COALESCE(AVG(r.rating), 0) DESC, MAX(uwi.added_at) DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM user_watchlist_items uwi
            WHERE uwi.user_id = :watchlistUserId
        """,
            nativeQuery = true
    )
    Page<UserWatchlistItem> findByUserOrderByAverageRatingDesc(
            @Param("watchlistUserId") Long watchlistUserId,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT uwi.*
            FROM user_watchlist_items uwi
            LEFT JOIN user_reviews r
                ON r.tmdb_id = uwi.item_id
               AND LOWER(
                    CASE r.media_type
                        WHEN 0 THEN 'movie'
                        WHEN 1 THEN 'tv'
                        ELSE NULL
                    END
               ) = LOWER(CAST(uwi.type AS text))
            WHERE uwi.user_id = :watchlistUserId
            GROUP BY uwi.id
            ORDER BY COUNT(r.id) DESC, MAX(uwi.added_at) DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM user_watchlist_items uwi
            WHERE uwi.user_id = :watchlistUserId
        """,
            nativeQuery = true
    )
    Page<UserWatchlistItem> findByUserOrderByRatingsCountDesc(
            @Param("watchlistUserId") Long watchlistUserId,
            Pageable pageable
    );
}