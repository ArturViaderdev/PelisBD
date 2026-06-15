package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.UserWatchedItem;
import com.arturviader.pelisbdapi.model.UserWatchlistItem;

public class UserWatchedMapper {
    public static WatchedItemDto toDto(UserWatchedItem item){
        return new WatchedItemDto(  item.getId(),
                item.getType().name(),
                item.getItemId(),
                item.getWatchedAt());
    }

    public WatchedItemDto toDto(UserWatchlistItem item) {
        return new WatchedItemDto(
                item.getId(),
                item.getType().name(),
                item.getItemId(),
                item.getAddedAt()
        );
    }
}
