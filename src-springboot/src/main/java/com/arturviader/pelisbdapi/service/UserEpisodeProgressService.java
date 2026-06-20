package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.UserEpisodeProgressDto;

import java.util.List;

public interface UserEpisodeProgressService {
    UserEpisodeProgressDto markEpisode(Long tvId, Integer seasonNumber, Integer episodeNumber, Boolean watched);

    List<UserEpisodeProgressDto> getSeasonProgress(Long tvId, Integer seasonNumber);

    boolean getEpisodeWatched(Long user,Long tvId,Integer seasonNumber, Integer episodeNumber);
}
