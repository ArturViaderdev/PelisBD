package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.Movie3DDto;
import com.arturviader.pelisbdapi.dto.Movie3DPendingDto;
import com.arturviader.pelisbdapi.dto.Movie3DStatusDto;
import com.arturviader.pelisbdapi.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface Movie3DService {

    // Público
    List<Long> getApprovedTmdbIds();

    Page<Movie3DDto> getApproved(int page, int size, User userOrNull);

    Movie3DStatusDto getStatus(Long tmdbId, User userOrNull);

    // Usuario
    void requestMark(User user, Long tmdbId);

    void cancelRequest(User user, Long tmdbId);

    // Administrador
    List<Movie3DPendingDto> getPendingRequests();

    void approve(Long tmdbId, User admin);

    void reject(Long tmdbId);

    void remove3D(Long tmdbId);
}
