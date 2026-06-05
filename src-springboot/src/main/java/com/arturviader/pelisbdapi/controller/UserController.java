package com.arturviader.pelisbdapi.controller;

import com.arturviader.pelisbdapi.dto.UserResponse;
import com.arturviader.pelisbdapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

   /* @GetMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse register(@RequestParam())*/

}
