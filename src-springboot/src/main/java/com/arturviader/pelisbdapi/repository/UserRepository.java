package com.arturviader.pelisbdapi.repository;

import com.arturviader.pelisbdapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String username);
    Optional<User> findByUserName(String userName);
}
