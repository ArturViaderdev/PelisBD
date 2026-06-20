package com.arturviader.pelisbdapi.mapper;

import com.arturviader.pelisbdapi.dto.UserEpisodeProgressDto;
import com.arturviader.pelisbdapi.model.UserEpisodeProgress;

public class ProgressMapper {
    public static UserEpisodeProgressDto toDto(UserEpisodeProgress progress) {
        return new UserEpisodeProgressDto(progress.getId(),progress.getTvId(),progress.getSeasonNumber(),progress.getEpisodeNumber(),progress.getWatched(),progress.getWatchedAt());
    }
}
