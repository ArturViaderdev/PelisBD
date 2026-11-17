package com.arturviader.pelisbdapi.dto;

/**
 * Estado 3D de una película para el detalle.
 * myRequestStatus: PENDING / APPROVED / REJECTED, o null si el usuario no la ha
 * propuesto o no ha iniciado sesión.
 */
public record Movie3DStatusDto(
        boolean is3d,
        String myRequestStatus
) {
}
