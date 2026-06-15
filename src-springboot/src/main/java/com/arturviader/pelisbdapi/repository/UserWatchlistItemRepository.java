package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchlistItemRepository extends JpaRepository<UserWatchlistItem, Long> {
    Optional<UserWatchlistItem> findByUserAndTypeAndItemId(User user, MediaType type, Long itemId);
    List<UserWatchlistItem> findByUser(User user);
    List<UserWatchlistItem> findByTypeAndItemId(MediaType type, Long itemId);
    boolean existsByUserAndItemIdAndType(User user, Long itemId, MediaType type);
}
