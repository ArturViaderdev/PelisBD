package com.arturviader.pelisbdapi.security;

import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Buscando usuario con: '" + username + "'");

        // Intenta buscar por username primero
        Optional<User> userOpt = userRepository.findByUserName(username);

        // Si no, busca por email
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }

        User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new JwtUserDetails(user);
    }
}

