package com.arturviader.pelisbdapi.dto;

import java.time.LocalDateTime;

/** Proyección de la consulta agrupada de propuestas 3D pendientes. */
public interface Movie3DPendingProjection {
    Long getTmdbId();
    String getTitle();
    String getPosterPath();
    String getReleaseDate();
    Long getRequestCount();
    LocalDateTime getFirstRequestedAt();
}
