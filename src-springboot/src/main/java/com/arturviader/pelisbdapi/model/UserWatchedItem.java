package com.arturviader.pelisbdapi.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;


@Entity
@Table(name = "user_watched_items")
public class UserWatchedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "watched_at")
    private LocalDateTime watchedAt;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }

    public UserWatchedItem () {
    }
}