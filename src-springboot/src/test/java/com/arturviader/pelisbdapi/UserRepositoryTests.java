package com.arturviader.pelisbdapi;

import com.arturviader.pelisbdapi.model.Role;
import com.arturviader.pelisbdapi.model.User;
import com.arturviader.pelisbdapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("juan@mail.es");
        user.setUserName("Juan");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole(Role.USER);
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        entityManager.persistAndFlush(user);
        Optional<User> result = userRepository.findByEmail("juan@mail.es");
        assertTrue(result.isPresent());
        assertEquals("juan@mail.es", result.get().getEmail());
        assertEquals("Juan", result.get().getUserName());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("noexiste@mail.es");
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        entityManager.persistAndFlush(user);
        boolean exists = userRepository.existsByEmail("juan@mail.es");
        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("noexiste@mail.es");
        assertFalse(exists);
    }

    @Test
    void existsByUserName_shouldReturnTrue_whenUserNameExists() {
        entityManager.persistAndFlush(user);
        boolean exists = userRepository.existsByUserName("Juan");
        assertTrue(exists);
    }

    @Test
    void existsByUserName_shouldReturnFalse_whenUserNameDoesNotExist() {
        boolean exists = userRepository.existsByUserName("Pedro");
        assertFalse(exists);
    }

    @Test
    void save_shouldThrowException_whenEmailIsDuplicated() {
        entityManager.persistAndFlush(user);
        User duplicated = new User();
        duplicated.setEmail("juan@mail.es");
        duplicated.setUserName("Otro");
        duplicated.setPassword("encodedPassword");
        duplicated.setEnabled(true);
        duplicated.setRole(Role.USER);
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicated);
        });
    }
}
