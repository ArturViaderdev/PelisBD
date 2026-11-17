package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.dto.Movie3DPendingProjection;
import com.arturviader.pelisbdapi.model.Movie3DRequest;
import com.arturviader.pelisbdapi.model.Request3DStatus;
import com.arturviader.pelisbdapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface Movie3DRequestRepository extends JpaRepository<Movie3DRequest, Long> {

    Optional<Movie3DRequest> findByUserAndTmdbId(User user, Long tmdbId);

    Optional<Movie3DRequest> findFirstByTmdbIdOrderByCreatedAtDesc(Long tmdbId);

    /**
     * Cola de propuestas pendientes agrupadas por película, con el número de
     * usuarios que la han propuesto. Las más pedidas primero.
     */
    @Query("""
            select r.tmdbId as tmdbId,
                   max(r.title) as title,
                   max(r.posterPath) as posterPath,
                   max(r.releaseDate) as releaseDate,
                   count(r) as requestCount,
                   min(r.createdAt) as firstRequestedAt
            from Movie3DRequest r
            where r.status = :status
            group by r.tmdbId
            order by count(r) desc, min(r.createdAt) asc
            """)
    List<Movie3DPendingProjection> findGroupedByStatus(@Param("status") Request3DStatus status);

    /** Cambia de estado todas las propuestas de una película que estén en un estado dado. */
    @Modifying
    @Query("""
            update Movie3DRequest r
            set r.status = :newStatus, r.reviewedAt = :reviewedAt
            where r.tmdbId = :tmdbId and r.status = :currentStatus
            """)
    int updateStatusForMovie(@Param("tmdbId") Long tmdbId,
                             @Param("currentStatus") Request3DStatus currentStatus,
                             @Param("newStatus") Request3DStatus newStatus,
                             @Param("reviewedAt") LocalDateTime reviewedAt);
}
