package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.dto.JwtResponse;
import com.arturviader.pelisbdapi.dto.LoginRequest;
import com.arturviader.pelisbdapi.dto.NewUserRequest;
import com.arturviader.pelisbdapi.dto.UserResponse;
import com.arturviader.pelisbdapi.exception.*;
import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.model.VerificationToken;
import com.arturviader.pelisbdapi.repository.UserRepository;
import com.arturviader.pelisbdapi.repository.VerificationTokenRepository;
import com.arturviader.pelisbdapi.service.EmailService;
import com.arturviader.pelisbdapi.service.JwtService;
import com.arturviader.pelisbdapi.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private NewUserRequest newUserRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        newUserRequest = new NewUserRequest("test@example.com", "Artur", "prova1234");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUserName("Artur");
        user.setPassword("encoded");
    }

    @Test
    public void registerUser_shouldCreateUserAndSendEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUserName("Artur")).thenReturn(false);
        when(passwordEncoder.encode("prova1234")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse response = userService.registerUser(newUserRequest);
        assertEquals("test@example.com", response.email());
        assertEquals("Artur", response.userName());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByUserName("Artur");
        verify(passwordEncoder).encode("prova1234");
        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).send(eq("test@example.com"), anyString(), contains("token="));
    }

    @Test
    public void registerUser_shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(AlreadyRegistreredEmail.class,
                () -> userService.registerUser(newUserRequest));
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
        verify(emailService, never()).send(any(), any(), any());
    }

    @Test
    public void registerUser_shouldThrowWhenUserNameAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUserName("Artur")).thenReturn(true);
        assertThrows(AlreadyRegistreredUserName.class,
                () -> userService.registerUser(newUserRequest));
        verify(userRepository).existsByUserName("Artur");
        verify(userRepository, never()).save(any());
    }

    @Test
    public void loginUser_shouldReturnJwtResponse_whenCredentialsAreCorrect() {
        LoginRequest dto = new LoginRequest("test@example.com", "prova1234");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUserName("Artur");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole(Role.USER);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        JwtResponse response = userService.loginUser(dto);
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.type());
        assertEquals(1L, response.id());
        assertEquals("test@example.com", response.email());
        assertEquals("Artur", response.userName());
        assertEquals("USER", response.role());
        verify(userRepository).findByEmail("test@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void loginUser_shouldThrowWhenUserNotFound() {
        LoginRequest dto = new LoginRequest("missing@example.com", "prova1234");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.loginUser(dto));

        verify(userRepository).findByEmail("missing@example.com");
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginUser_shouldThrowWhenPasswordIsWrong() {
        LoginRequest dto = new LoginRequest("test@example.com", "wrong");
        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(true);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        assertThrows(BadCredentialsException.class, () -> userService.loginUser(dto));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void confirmEmail_shouldEnableUserWhenTokenValid() {
        VerificationToken token = new VerificationToken();
        token.setToken("abc");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(verificationTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(token));
        userService.confirmEmail("abc");
        assertTrue(user.isEnabled()); // o el campo que uses
        verify(verificationTokenRepository).findByToken("abc");
        verify(userRepository).save(user);
    }

    @Test
    void confirmEmail_shouldThrowWhenTokenNotFound() {
        when(verificationTokenRepository.findByToken("bad"))
                .thenReturn(Optional.empty());
        assertThrows(TokenNotFoundException.class,
                () -> userService.confirmEmail("bad"));
    }

    @Test
    void confirmEmail_shouldThrowWhenTokenExpired() {
        VerificationToken token = new VerificationToken();
        token.setToken("expired");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(verificationTokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(token));
        assertThrows(TokenExpiredException.class,
                () -> userService.confirmEmail("expired"));
    }

    @Test
    void loginUser_shouldThrowWhenEmailNotConfirmed() {
        LoginRequest dto = new LoginRequest("test@example.com", "prova1234");
        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(false);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        assertThrows(EmailNotConfirmed.class, () -> userService.loginUser(dto));
        verify(userRepository).findByEmail("test@example.com");
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }
}
