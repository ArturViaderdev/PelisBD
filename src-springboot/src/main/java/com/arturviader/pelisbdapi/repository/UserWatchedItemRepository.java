package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.dto.GlobalWatchedItem;
import com.arturviader.pelisbdapi.dto.GlobalWatchedProjection;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchedItemRepository extends JpaRepository<UserWatchedItem, Long> {

    Optional<UserWatchedItem> findByUserAndTypeAndItemId(User user, MediaType type, Long itemId);

    List<UserWatchedItem> findByUser(User user);

    Page<UserWatchedItem> findByUser(User user, Pageable pageable);

    List<UserWatchedItem> findByUserAndType(User user, MediaType type);

    List<UserWatchedItem> findByTypeAndItemId(MediaType type, Long itemId);

    boolean existsByUserAndItemIdAndType(User user, Long itemId, MediaType mediaType);

    @Query("""
        SELECT new com.arturviader.pelisbdapi.dto.GlobalWatchedItem(
            w.itemId,
            w.type,
            MAX(w.watchedAt)
        )
        FROM UserWatchedItem w
        GROUP BY w.itemId, w.type
    """)
    List<GlobalWatchedItem> findGlobalWatchedDistinct();

    @Query(
            value = """
                SELECT wui.*
                FROM user_watched_items wui
                LEFT JOIN user_reviews r
                    ON r.tmdb_id = wui.item_id
                   AND LOWER(
                        CASE r.media_type
                            WHEN 0 THEN 'movie'
                            WHEN 1 THEN 'tv'
                            ELSE NULL
                        END
                   ) = LOWER(CAST(wui.type AS text))
                   AND r.user_id = :reviewUsername
                WHERE wui.user_id = :watchedUserId
                ORDER BY COALESCE(r.rating, 0) DESC, wui.watched_at DESC
            """,
            countQuery = """
                SELECT COUNT(*)
                FROM user_watched_items wui
                WHERE wui.user_id = :watchedUserId
            """,
            nativeQuery = true
    )
    Page<UserWatchedItem> findByUserOrderByOwnRatingDesc(
            @Param("watchedUserId") Long watchedUserId,
            @Param("reviewUsername") String reviewUsername,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT wui.*
                FROM user_watched_items wui
                LEFT JOIN user_reviews r
                    ON r.tmdb_id = wui.item_id
                   AND LOWER(
                        CASE r.media_type
                            WHEN 0 THEN 'movie'
                            WHEN 1 THEN 'tv'
                            ELSE NULL
                        END
                   ) = LOWER(CAST(wui.type AS text))
                WHERE wui.user_id = :watchedUserId
                GROUP BY wui.id
                ORDER BY COALESCE(AVG(r.rating), 0) DESC, MAX(wui.watched_at) DESC
            """,
            countQuery = """
                SELECT COUNT(*)
                FROM user_watched_items wui
                WHERE wui.user_id = :watchedUserId
            """,
            nativeQuery = true
    )
    Page<UserWatchedItem> findByUserOrderByAverageRatingDesc(
            @Param("watchedUserId") Long watchedUserId,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT wui.*
                FROM user_watched_items wui
                LEFT JOIN user_reviews r
                    ON r.tmdb_id = wui.item_id
                   AND LOWER(
                        CASE r.media_type
                            WHEN 0 THEN 'movie'
                            WHEN 1 THEN 'tv'
                            ELSE NULL
                        END
                   ) = LOWER(CAST(wui.type AS text))
                WHERE wui.user_id = :watchedUserId
                GROUP BY wui.id
                ORDER BY COUNT(r.id) DESC, MAX(wui.watched_at) DESC
            """,
            countQuery = """
                SELECT COUNT(*)
                FROM user_watched_items wui
                WHERE wui.user_id = :watchedUserId
            """,
            nativeQuery = true
    )
    Page<UserWatchedItem> findByUserOrderByRatingsCountDesc(
            @Param("watchedUserId") Long watchedUserId,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT new com.arturviader.pelisbdapi.dto.GlobalWatchedItem(
                w.itemId,
                w.type,
                MAX(w.watchedAt)
            )
            FROM UserWatchedItem w
            GROUP BY w.itemId, w.type
        """,
            countQuery = """
            SELECT COUNT(DISTINCT CONCAT(CAST(w.itemId as string), ':', CAST(w.type as string)))
            FROM UserWatchedItem w
        """
    )
    Page<GlobalWatchedItem> findGlobalWatchedDistinct(Pageable pageable);

    @Query(
            value = """
            SELECT
                wui.item_id AS itemId,
                wui.type AS type,
                MAX(wui.watched_at) AS watchedAt
            FROM user_watched_items wui
            GROUP BY wui.item_id, wui.type
            ORDER BY MAX(wui.watched_at) DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT wui.item_id, wui.type
                FROM user_watched_items wui
                GROUP BY wui.item_id, wui.type
            ) c
        """,
            nativeQuery = true
    )
    Page<GlobalWatchedProjection> findGlobalWatchedDistinctOrderByDateDesc(Pageable pageable);

    @Query(
            value = """
            SELECT
                wui.item_id AS itemId,
                wui.type AS type,
                MAX(wui.watched_at) AS watchedAt
            FROM user_watched_items wui
            LEFT JOIN user_reviews r
                ON r.tmdb_id = wui.item_id
               AND LOWER(
                    CASE r.media_type
                        WHEN 0 THEN 'movie'
                        WHEN 1 THEN 'tv'
                        ELSE NULL
                    END
               ) = LOWER(CAST(wui.type AS text))
            GROUP BY wui.item_id, wui.type
            ORDER BY COALESCE(AVG(r.rating), 0) DESC, MAX(wui.watched_at) DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT wui.item_id, wui.type
                FROM user_watched_items wui
                GROUP BY wui.item_id, wui.type
            ) c
        """,
            nativeQuery = true
    )
    Page<GlobalWatchedProjection> findGlobalWatchedDistinctOrderByAverageRatingDesc(Pageable pageable);

    @Query(
            value = """
            SELECT
                wui.item_id AS itemId,
                wui.type AS type,
                MAX(wui.watched_at) AS watchedAt
            FROM user_watched_items wui
            LEFT JOIN user_reviews r
                ON r.tmdb_id = wui.item_id
               AND LOWER(
                    CASE r.media_type
                        WHEN 0 THEN 'movie'
                        WHEN 1 THEN 'tv'
                        ELSE NULL
                    END
               ) = LOWER(CAST(wui.type AS text))
            GROUP BY wui.item_id, wui.type
            ORDER BY COUNT(r.id) DESC, MAX(wui.watched_at) DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT wui.item_id, wui.type
                FROM user_watched_items wui
                GROUP BY wui.item_id, wui.type
            ) c
        """,
            nativeQuery = true
    )
    Page<GlobalWatchedProjection> findGlobalWatchedDistinctOrderByRatingsCountDesc(Pageable pageable);

    @Query(
            value = """
            SELECT
                gw.item_id AS itemId,
                gw.type AS type,
                gw.watched_at AS watchedAt
            FROM (
                SELECT
                    wui.item_id,
                    wui.type,
                    MAX(wui.watched_at) AS watched_at,
                    LOWER(
                        COALESCE(
                            m.title,
                            s.title,
                            ''
                        )
                    ) AS sort_title
                FROM user_watched_items wui
                LEFT JOIN movies m
                    ON wui.type = 'movie'
                   AND m.id = wui.item_id
                LEFT JOIN series s
                    ON wui.type = 'tv'
                   AND s.id = wui.item_id
                GROUP BY
                    wui.item_id,
                    wui.type,
                    m.title,
                    s.title
            ) gw
            ORDER BY gw.sort_title ASC,
                     gw.watched_at DESC,
                     gw.item_id ASC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT wui.item_id, wui.type
                FROM user_watched_items wui
                GROUP BY wui.item_id, wui.type
            ) c
        """,
            nativeQuery = true
    )
    Page<GlobalWatchedProjection> findGlobalWatchedDistinctOrderByTitleAsc(Pageable pageable);
}