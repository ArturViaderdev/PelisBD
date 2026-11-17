package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.SerieDetailTMDB;
import com.arturviader.pelisbdapi.dto.SeriesInProgressDto;
import com.arturviader.pelisbdapi.dto.UserEpisodeProgressDto;
import com.arturviader.pelisbdapi.mapper.ProgressMapper;
import com.arturviader.pelisbdapi.model.Serie;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.UserEpisodeProgress;
import com.arturviader.pelisbdapi.repository.UserEpisodeProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserEpisodeProgressServiceImpl implements UserEpisodeProgressService {

    @Autowired
    private UserEpisodeProgressRepository progressRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TvService tvService;

    // @Lazy rompe la dependencia circular: TMDBServiceImpl también usa este servicio
    @Autowired
    @Lazy
    private TMDBService tmdbService;

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
            // Guarda la serie en la tabla local la primera vez que se ve un capítulo
            ensureSerieSaved(tvId);
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

    @Override
    public List<SeriesInProgressDto> getSeriesInProgress() {
        User user = userService.getCurrentUser();
        List<UserEpisodeProgress> watchedEpisodes = progressRepository.findByUserIdAndWatchedTrue(user.getId());

        Map<Long, List<UserEpisodeProgress>> byTv = watchedEpisodes.stream()
                .collect(Collectors.groupingBy(UserEpisodeProgress::getTvId));

        // Capítulo más avanzado: mayor temporada y, dentro de ella, mayor número de capítulo
        Comparator<UserEpisodeProgress> byPosition = Comparator
                .comparing(UserEpisodeProgress::getSeasonNumber)
                .thenComparing(UserEpisodeProgress::getEpisodeNumber);

        List<SeriesInProgressDto> result = new ArrayList<>();
        for (Map.Entry<Long, List<UserEpisodeProgress>> entry : byTv.entrySet()) {
            Long tvId = entry.getKey();
            List<UserEpisodeProgress> episodes = entry.getValue();

            UserEpisodeProgress furthest = episodes.stream().max(byPosition).orElseThrow();
            LocalDateTime lastWatchedAt = episodes.stream()
                    .map(UserEpisodeProgress::getWatchedAt)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            // Título y póster desde la tabla local. Las series con capítulos marcados
            // antes de existir esta sección se guardan aquí la primera vez.
            Optional<Serie> serie = ensureSerieSaved(tvId);
            String name = serie.map(Serie::getTitle).orElse(null);
            String posterPath = serie.map(Serie::getPosterPath).orElse(null);

            result.add(new SeriesInProgressDto(
                    tvId,
                    name,
                    posterPath,
                    furthest.getSeasonNumber(),
                    furthest.getEpisodeNumber(),
                    (long) episodes.size(),
                    lastWatchedAt));
        }

        // Las vistas más recientemente primero
        result.sort(Comparator.comparing(SeriesInProgressDto::lastWatchedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    /**
     * Devuelve la serie de la tabla local; si no existe la pide a TMDB y la guarda.
     * Solo se consulta TMDB cuando la serie aún no está guardada.
     * Si TMDB falla no se interrumpe la operación (devuelve vacío).
     */
    private Optional<Serie> ensureSerieSaved(Long tvId) {
        Optional<Serie> existing = tvService.findById(tvId);
        if (existing.isPresent()) {
            return existing;
        }
        try {
            SerieDetailTMDB detail = tmdbService.getTvShowDetail(tvId);
            if (detail == null || detail.getName() == null) {
                return Optional.empty();
            }
            return Optional.of(tvService.saveIfNotExists(detail, "tv"));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
