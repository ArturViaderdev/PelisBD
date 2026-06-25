package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;

public class UserMapper {
    public static User toEntity(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setUserName(request.userName());
        user.setPassword(request.password());
        user.setRole(Role.USER);
        user.setEnabled(false);
        return user;
    }

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUserName(), user.getRole().toString());
    }
}
