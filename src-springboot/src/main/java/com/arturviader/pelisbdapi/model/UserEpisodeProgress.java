package com.arturviader.pelisbdapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_episode_progress")
public class UserEpisodeProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tv_id", nullable = false)
    private Long tvId;

    @Column(name = "season_number", nullable = false)
    private Integer seasonNumber;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(name = "watched")
    private Boolean watched = false;

    @Column(name = "watched_at")
    private LocalDateTime watchedAt;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getTvId() { return tvId; }
    public void setTvId(Long tvId) { this.tvId = tvId; }

    public Integer getSeasonNumber() { return seasonNumber; }
    public void setSeasonNumber(Integer seasonNumber) { this.seasonNumber = seasonNumber; }

    public Integer getEpisodeNumber() { return episodeNumber; }
    public void setEpisodeNumber(Integer episodeNumber) { this.episodeNumber = episodeNumber; }

    public Boolean getWatched() { return watched; }
    public void setWatched(Boolean watched) { this.watched = watched; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}