package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.JwtResponse;
import com.arturviader.pelisbdapi.dto.LoginRequest;
import com.arturviader.pelisbdapi.dto.NewUserRequest;
import com.arturviader.pelisbdapi.dto.UserResponse;
import com.arturviader.pelisbdapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<Void> register(@RequestBody NewUserRequest user)
    {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request){
        JwtResponse jwtResponse = userService.loginUser(request);
        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/api/auth/confirmemail")
    public ResponseEntity<String> confirmEmail(@RequestParam(name="token",defaultValue="") String token)
    {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email confirmado");
    }
}
