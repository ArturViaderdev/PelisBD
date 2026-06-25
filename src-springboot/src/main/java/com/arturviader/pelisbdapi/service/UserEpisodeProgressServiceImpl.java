package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.UserEpisodeProgressDto;
import com.arturviader.pelisbdapi.mapper.ProgressMapper;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserEpisodeProgress;
import com.arturviader.pelisbdapi.repository.UserEpisodeProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserEpisodeProgressServiceImpl implements UserEpisodeProgressService {

    @Autowired
    private UserEpisodeProgressRepository progressRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserEpisodeProgressDto markEpisode(Long tvId, Integer seasonNumber, Integer episodeNumber, Boolean watched) {
        User user = userService.getCurrentUser();

        Optional<UserEpisodeProgress> progressVar = progressRepository.findByUserIdAndTvIdAndSeasonNumberAndEpisodeNumber(
                user.getId(), tvId, seasonNumber, episodeNumber);
        UserEpisodeProgress progress;
        if (progressVar.isEmpty()) {
            progress = new UserEpisodeProgress();
            progress.setUser(user);
            progress.setTvId(tvId);
            progress.setSeasonNumber(seasonNumber);
            progress.setEpisodeNumber(episodeNumber);
        }
        else
        {
            progress = progressVar.get();
        }
        progress.setWatched(watched);
        if (watched) {
            progress.setWatchedAt(LocalDateTime.now());
        }
        progressRepository.save(progress);

        return ProgressMapper.toDto(progress);
    }

    @Override
    public List<UserEpisodeProgressDto> getSeasonProgress(Long tvId, Integer seasonNumber) {
        User user = userService.getCurrentUser();
        List<UserEpisodeProgress> progressList = progressRepository.findByUserIdAndTvIdAndSeasonNumber(
                user.getId(), tvId, seasonNumber);

        return progressList.stream()
                .map(ProgressMapper::toDto)
                .toList();
    }

    @Override
    public boolean getEpisodeWatched(Long user, Long tvId, Integer seasonNumber, Integer episodeNumber) {
        Optional<UserEpisodeProgress> progress = progressRepository.findByUserIdAndTvIdAndSeasonNumberAndEpisodeNumber(user,tvId,seasonNumber,episodeNumber);
        boolean watched;
        if(progress.isEmpty())
        {
            watched=false;
        }
        else
        {
            watched=progress.get().getWatched();
        }
        return watched;
    }
}