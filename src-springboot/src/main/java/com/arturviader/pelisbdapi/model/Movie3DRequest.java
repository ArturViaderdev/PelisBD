package com.arturviader.pelisbdapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Propuesta de un usuario para marcar una película como 3D.
 * Un usuario solo puede tener una propuesta por película.
 */
@Entity
@Table(name = "movie_3d_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tmdb_id"}))
public class Movie3DRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    // Datos básicos de la película guardados al proponerla, para que el
    // administrador vea la cola sin hacer llamadas a TMDB.
    @Column(nullable = false)
    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "release_date")
    private String releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Request3DStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Request3DStatus.PENDING;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getTmdbId() { return tmdbId; }
    public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public Request3DStatus getStatus() { return status; }
    public void setStatus(Request3DStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
