package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.Movie3DDto;
import com.arturviader.pelisbdapi.dto.Movie3DPendingDto;
import com.arturviader.pelisbdapi.dto.Movie3DStatusDto;
import com.arturviader.pelisbdapi.dto.MovieDetailTMDB;
import com.arturviader.pelisbdapi.exception.ResourceNotFoundException;
import com.arturviader.pelisbdapi.model.MediaType;
import com.arturviader.pelisbdapi.model.Movie3D;
import com.arturviader.pelisbdapi.model.Movie3DRequest;
import com.arturviader.pelisbdapi.model.Request3DStatus;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.Movie3DRepository;
import com.arturviader.pelisbdapi.repository.Movie3DRequestRepository;
import com.arturviader.pelisbdapi.repository.UserWatchedItemRepository;
import com.arturviader.pelisbdapi.repository.UserWatchlistItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Movie3DServiceImpl implements Movie3DService {

    private final Movie3DRepository movie3DRepository;
    private final Movie3DRequestRepository requestRepository;
    private final TMDBService tmdbService;
    private final UserWatchedItemRepository watchedRepository;
    private final UserWatchlistItemRepository watchlistRepository;

    public Movie3DServiceImpl(Movie3DRepository movie3DRepository,
                              Movie3DRequestRepository requestRepository,
                              TMDBService tmdbService,
                              UserWatchedItemRepository watchedRepository,
                              UserWatchlistItemRepository watchlistRepository) {
        this.movie3DRepository = movie3DRepository;
        this.requestRepository = requestRepository;
        this.tmdbService = tmdbService;
        this.watchedRepository = watchedRepository;
        this.watchlistRepository = watchlistRepository;
    }

    // ---------- Público ----------

    @Override
    @Transactional(readOnly = true)
    public List<Long> getApprovedTmdbIds() {
        return movie3DRepository.findAllTmdbIds();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie3DDto> getApproved(int page, int size, User userOrNull) {
        // page llega empezando en 1, como en el resto de listados
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        PageRequest pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "approvedAt"));

        Page<Movie3D> movies = movie3DRepository.findAll(pageable);

        // Flags del usuario: 2 consultas para toda la página (no una por película).
        // Sin sesión no se hace ninguna consulta de usuario.
        Set<Long> watchedIds = Collections.emptySet();
        Set<Long> watchlistIds = Collections.emptySet();

        if (userOrNull != null && !movies.isEmpty()) {
            Set<Long> tmdbIds = movies.getContent().stream()
                    .map(Movie3D::getTmdbId)
                    .collect(Collectors.toSet());

            watchedIds = watchedRepository.findByUserIdAndItemIdIn(userOrNull.getId(), tmdbIds).stream()
                    .filter(w -> w.getType() == MediaType.movie)
                    .map(w -> w.getItemId())
                    .collect(Collectors.toSet());

            watchlistIds = watchlistRepository.findByUserIdAndItemIdIn(userOrNull.getId(), tmdbIds).stream()
                    .filter(w -> w.getType() == MediaType.movie)
                    .map(w -> w.getItemId())
                    .collect(Collectors.toSet());
        }

        final Set<Long> watched = watchedIds;
        final Set<Long> watchlisted = watchlistIds;

        return movies.map(m -> new Movie3DDto(
                m.getTmdbId(), m.getTitle(), m.getPosterPath(), m.getReleaseDate(), m.getApprovedAt(),
                watched.contains(m.getTmdbId()),
                watchlisted.contains(m.getTmdbId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Movie3DStatusDto getStatus(Long tmdbId, User userOrNull) {
        boolean is3d = movie3DRepository.existsByTmdbId(tmdbId);

        String myStatus = null;
        if (userOrNull != null) {
            myStatus = requestRepository.findByUserAndTmdbId(userOrNull, tmdbId)
                    .map(r -> r.getStatus().name())
                    .orElse(null);
        }
        return new Movie3DStatusDto(is3d, myStatus);
    }

    // ---------- Usuario ----------

    @Override
    @Transactional
    public void requestMark(User user, Long tmdbId) {
        // Ya es 3D: no hace falta proponerla
        if (movie3DRepository.existsByTmdbId(tmdbId)) {
            return;
        }

        Optional<Movie3DRequest> existing = requestRepository.findByUserAndTmdbId(user, tmdbId);
        if (existing.isPresent()) {
            Movie3DRequest request = existing.get();
            if (request.getStatus() != Request3DStatus.PENDING) {
                // Rechazada antes (o aprobada y luego retirada): se vuelve a proponer
                request.setStatus(Request3DStatus.PENDING);
                request.setCreatedAt(LocalDateTime.now());
                request.setReviewedAt(null);
                requestRepository.save(request);
            }
            return;
        }

        MovieDetailTMDB movie = fetchMovie(tmdbId);

        Movie3DRequest request = new Movie3DRequest();
        request.setUser(user);
        request.setTmdbId(tmdbId);
        request.setTitle(movie.getTitle());
        request.setPosterPath(movie.getPoster_path());
        request.setReleaseDate(movie.getRelease_date());
        request.setStatus(Request3DStatus.PENDING);
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void cancelRequest(User user, Long tmdbId) {
        requestRepository.findByUserAndTmdbId(user, tmdbId)
                .filter(r -> r.getStatus() == Request3DStatus.PENDING)
                .ifPresent(requestRepository::delete);
    }

    // ---------- Administrador ----------

    @Override
    @Transactional(readOnly = true)
    public List<Movie3DPendingDto> getPendingRequests() {
        return requestRepository.findGroupedByStatus(Request3DStatus.PENDING).stream()
                .map(p -> new Movie3DPendingDto(
                        p.getTmdbId(),
                        p.getTitle(),
                        p.getPosterPath(),
                        p.getReleaseDate(),
                        p.getRequestCount() != null ? p.getRequestCount() : 0L,
                        p.getFirstRequestedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void approve(Long tmdbId, User admin) {
        if (!movie3DRepository.existsByTmdbId(tmdbId)) {
            Movie3D movie3D = new Movie3D();
            movie3D.setTmdbId(tmdbId);
            movie3D.setApprovedBy(admin != null ? admin.getUserName() : null);

            // Si alguien la propuso, se reutilizan sus datos y no se llama a TMDB.
            // Si el admin la marca directamente sin propuesta, se piden a TMDB.
            Optional<Movie3DRequest> anyRequest = requestRepository.findFirstByTmdbIdOrderByCreatedAtDesc(tmdbId);
            if (anyRequest.isPresent()) {
                Movie3DRequest r = anyRequest.get();
                movie3D.setTitle(r.getTitle());
                movie3D.setPosterPath(r.getPosterPath());
                movie3D.setReleaseDate(r.getReleaseDate());
            } else {
                MovieDetailTMDB movie = fetchMovie(tmdbId);
                movie3D.setTitle(movie.getTitle());
                movie3D.setPosterPath(movie.getPoster_path());
                movie3D.setReleaseDate(movie.getRelease_date());
            }
            movie3DRepository.save(movie3D);
        }

        requestRepository.updateStatusForMovie(tmdbId, Request3DStatus.PENDING,
                Request3DStatus.APPROVED, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void reject(Long tmdbId) {
        requestRepository.updateStatusForMovie(tmdbId, Request3DStatus.PENDING,
                Request3DStatus.REJECTED, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void remove3D(Long tmdbId) {
        movie3DRepository.findByTmdbId(tmdbId).ifPresent(movie3DRepository::delete);
    }

    private MovieDetailTMDB fetchMovie(Long tmdbId) {
        MovieDetailTMDB movie;
        try {
            movie = tmdbService.getMovieById(tmdbId);
        } catch (RuntimeException ex) {
            movie = null;
        }
        if (movie == null || movie.getTitle() == null) {
            throw new ResourceNotFoundException("Película no encontrada en TMDB: " + tmdbId);
        }
        return movie;
    }
}
