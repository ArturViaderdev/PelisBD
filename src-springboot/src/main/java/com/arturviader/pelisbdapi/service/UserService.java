package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.JwtResponse;
import com.arturviader.pelisbdapi.dto.LoginRequest;
import com.arturviader.pelisbdapi.dto.NewUserRequest;
import com.arturviader.pelisbdapi.dto.UserResponse;
import com.arturviader.pelisbdapi.model.User;

public interface UserService {
    UserResponse registerUser(NewUserRequest dto);
    JwtResponse loginUser(LoginRequest dto);
    void confirmEmail(String token);
    User getCurrentUser();
}
