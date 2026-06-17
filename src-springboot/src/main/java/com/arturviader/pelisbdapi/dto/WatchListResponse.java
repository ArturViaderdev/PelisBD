package com.arturviader.pelisbdapi.dto;

import java.util.List;

public record WatchListResponse(
        List<WatchlistItemDto> content,
        int page,
        int size,
        int totalElements,
        int totalPages,
        boolean last,
        boolean first
) {

}
