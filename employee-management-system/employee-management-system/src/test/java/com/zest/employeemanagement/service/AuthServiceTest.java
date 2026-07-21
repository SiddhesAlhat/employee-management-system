package com.zest.employeemanagement.service;

import com.zest.employeemanagement.dto.RegisterRequest;
import com.zest.employeemanagement.entity.User;
import com.zest.employeemanagement.exception.DuplicateResourceException;
import com.zest.employeemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("rutuja");
        registerRequest.setEmail("rutuja@example.com");
        registerRequest.setPassword("securePass123");
    }

    @Test
    void register_shouldCreateUser_whenUsernameAndEmailAreUnique() {
        when(userRepository.existsByUsername("rutuja")).thenReturn(false);
        when(userRepository.existsByEmail("rutuja@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securePass123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(registerRequest);

        assertEquals("rutuja", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals("ROLE_USER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("rutuja")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByUsername("rutuja")).thenReturn(false);
        when(userRepository.existsByEmail("rutuja@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
