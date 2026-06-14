package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserWatchedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWatchedItemRepository extends JpaRepository<UserWatchedItem, Long>{
    Optional<UserWatchedItem> findByUserAndTypeAndItemId(User user, MediaType type, Long itemId);
    List<UserWatchedItem> findByUser(User user);
    List<UserWatchedItem> findByUserAndType(User user, MediaType type);
    List<UserWatchedItem> findByTypeAndItemId(MediaType type, Long itemId);
}
