package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.dto.GlobalWatchedItem;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchedItemRepository extends JpaRepository<UserWatchedItem, Long> {
    Optional<UserWatchedItem> findByUserAndTypeAndItemId(User user, MediaType type, Long itemId);

    List<UserWatchedItem> findByUser(User user);

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
}
