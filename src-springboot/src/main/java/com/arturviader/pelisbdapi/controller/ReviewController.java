package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.model.Review;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.service.ReviewService;
import com.arturviader.pelisbdapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{type}/{id}/rate")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Map<String, Object>> rateItem(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {

        Integer rating = request.get("rating");
        if (rating == null || rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "La puntuación debe ser del 1 al 5."));
        }

        String userId = getCurrentUser().getUserName();
        Review review = reviewService.rateItem(userId, id, type, rating);
        Map<String, Object> ratings = reviewService.getRatings(userId, id, type);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{type}/{id}/ratings")
    @Operation(security = @SecurityRequirement(name = "api_key"))
    public ResponseEntity<Map<String, Object>> getItemRatings(
            @PathVariable String type,
            @PathVariable Long id) {
        String userId = getCurrentUser().getUserName();
        Map<String, Object> ratings = reviewService.getRatings(userId, id, type);
        return ResponseEntity.ok(ratings);
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
}