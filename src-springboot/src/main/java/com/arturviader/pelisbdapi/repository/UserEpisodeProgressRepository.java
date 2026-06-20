package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.UserEpisodeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEpisodeProgressRepository extends JpaRepository<UserEpisodeProgress, Long> {

    Optional<UserEpisodeProgress> findByUserIdAndTvIdAndSeasonNumberAndEpisodeNumber(
            Long userId, Long tvId, Integer seasonNumber, Integer episodeNumber);

    List<UserEpisodeProgress> findByUserIdAndTvId(Long userId, Long tvId);

    List<UserEpisodeProgress> findByUserIdAndTvIdAndSeasonNumber(
            Long userId, Long tvId, Integer seasonNumber);


}
