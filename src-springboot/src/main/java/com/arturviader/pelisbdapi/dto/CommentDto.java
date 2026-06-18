package com.arturviader.pelisbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    @JsonProperty("userId")
    private Long userId;
    private String userName;
    private String text;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private LocalDateTime createdAt;
    private boolean isEditable;

    public CommentDto(Long id, Long userId, String userName, String text, boolean isPublic, LocalDateTime createdAt, boolean isEditable) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.isEditable = isEditable;
    }

    public CommentDto()
    {

    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}