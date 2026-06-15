package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserEpisodeProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEpisodeProgressRepository extends JpaRepository<UserEpisodeProgress, Long>{
    Optional<UserEpisodeProgress> findByUserAndTvIdAndSeasonNumberAndEpisodeNumber(
            User user, Long tvId, Integer seasonNumber, Integer episodeNumber);
    Optional<UserEpisodeProgress> findByTvIdAndSeasonNumberAndEpisodeNumber(
            Long tvId, Integer seasonNumber, Integer episodeNumber);
    List<UserEpisodeProgress> findByTvIdAndWatched(Long tvId, Boolean watched);
    List<UserEpisodeProgress> findByTvIdAndSeasonNumber(Long tvId, Integer seasonNumber);
    List<UserEpisodeProgress> findByUser(User user);
}
