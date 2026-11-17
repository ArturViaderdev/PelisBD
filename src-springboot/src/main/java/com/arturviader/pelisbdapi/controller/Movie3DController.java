package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.Movie3DDto;
import com.arturviader.pelisbdapi.dto.Movie3DPendingDto;
import com.arturviader.pelisbdapi.dto.Movie3DStatusDto;
import com.arturviader.pelisbdapi.exception.NoUserAuthenticated;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.Movie3DService;
import com.arturviader.pelisbdapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Películas 3D:
 *  - Los usuarios proponen una película como 3D.
 *  - El administrador aprueba o rechaza (ve cuántos usuarios la han propuesto).
 *  - Una vez aprobada se muestra como 3D para todos.
 */
@RestController
public class Movie3DController {

    private final Movie3DService movie3DService;
    private final UserService userService;

    public Movie3DController(Movie3DService movie3DService, UserService userService) {
        this.movie3DService = movie3DService;
        this.userService = userService;
    }

    // ---------- Público ----------

    @GetMapping("/api/movies3d")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Page<Movie3DDto>> getApproved(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        // Si hay usuario logueado, cada película lleva sus flags watched / watchListed
        return ResponseEntity.ok(movie3DService.getApproved(page, size, getOptionalCurrentUser()));
    }

    @GetMapping("/api/movies3d/ids")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<List<Long>> getApprovedIds() {
        return ResponseEntity.ok(movie3DService.getApprovedTmdbIds());
    }

    @GetMapping("/api/movies3d/{tmdbId}/status")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Movie3DStatusDto> getStatus(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(movie3DService.getStatus(tmdbId, getOptionalCurrentUser()));
    }

    // ---------- Usuario logueado ----------

    @PostMapping("/api/user/movies3d/{tmdbId}/request")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Movie3DStatusDto> requestMark(@PathVariable Long tmdbId) {
        User user = userService.getCurrentUser();
        movie3DService.requestMark(user, tmdbId);
        return ResponseEntity.ok(movie3DService.getStatus(tmdbId, user));
    }

    @DeleteMapping("/api/user/movies3d/{tmdbId}/request")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Movie3DStatusDto> cancelRequest(@PathVariable Long tmdbId) {
        User user = userService.getCurrentUser();
        movie3DService.cancelRequest(user, tmdbId);
        return ResponseEntity.ok(movie3DService.getStatus(tmdbId, user));
    }

    // ---------- Administrador ----------

    @GetMapping("/api/admin/movies3d/requests")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<List<Movie3DPendingDto>> getPendingRequests() {
        return ResponseEntity.ok(movie3DService.getPendingRequests());
    }

    /** Aprueba la película como 3D. Sirve también para marcarla directamente sin propuesta. */
    @PostMapping("/api/admin/movies3d/{tmdbId}/approve")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> approve(@PathVariable Long tmdbId) {
        movie3DService.approve(tmdbId, getOptionalCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/movies3d/{tmdbId}/reject")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> reject(@PathVariable Long tmdbId) {
        movie3DService.reject(tmdbId);
        return ResponseEntity.noContent().build();
    }

    /** Quita la marca 3D de una película ya aprobada. */
    @DeleteMapping("/api/admin/movies3d/{tmdbId}")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Void> remove3D(@PathVariable Long tmdbId) {
        movie3DService.remove3D(tmdbId);
        return ResponseEntity.noContent().build();
    }

    /** Usuario autenticado o null si es anónimo (para endpoints públicos). */
    private User getOptionalCurrentUser() {
        try {
            return userService.getCurrentUser();
        } catch (NoUserAuthenticated | org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            return null;
        }
    }
}
