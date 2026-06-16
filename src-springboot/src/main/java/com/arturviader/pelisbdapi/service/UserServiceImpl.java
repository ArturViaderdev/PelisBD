package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.*;
import com.arturviader.pelisbdapi.exception.*;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.VerificationToken;
import com.arturviader.pelisbdapi.repository.UserRepository;
import com.arturviader.pelisbdapi.repository.VerificationTokenRepository;
import com.arturviader.pelisbdapi.security.JwtUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, JwtService jwtService, EmailService emailService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserResponse registerUser(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new AlreadyRegistreredEmail();
        }
        if (userRepository.existsByUserName(dto.userName())){
            throw new AlreadyRegistreredUserName();
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        String link = "http://localhost:3000/api/auth/confirmemail?token=" + token;

        emailService.send(
                user.getEmail(),
                "Confirma tu cuenta",
                "Haz clic en el siguiente enlace para activar tu cuenta: " + link
        );

        return UserMapper.toDto(user);
    }

    @Override
    public JwtResponse loginUser(LoginRequest dto) {
        System.out.println("🔍 Buscando usuario service con: '" + dto.email() + "'");
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(UserNotFound::new);

        if (!user.isEnabled()) {
            throw new EmailNotConfirmed();
        }


        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }


        UserDetails userDetails = new JwtUserDetails(user);


        String jwt = jwtService.generateToken(userDetails);


        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(auth -> auth.replace("ROLE_", ""))
                .orElse("USER");

        return new JwtResponse(
                jwt,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                role
        );
    }


    @Override
    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(TokenNotFoundException::new);

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new NoUserAuthenticated();
        }
        String username = authentication.getName();
        System.out.println("Username" + username);
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFound());
    }
}
