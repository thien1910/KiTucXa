package com.project.KiTucXa.service;

import com.nimbusds.jose.JOSEException;
import com.project.KiTucXa.Dto.Request.AuthenticationRequest;
import com.project.KiTucXa.Dto.Request.IntrospectRequest;
import com.project.KiTucXa.Dto.Response.AuthenticationResponse;
import com.project.KiTucXa.Dto.Response.IntrospectResponse;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserName("testuser");
        mockUser.setPassWord(passwordEncoder.encode("password123"));
        mockUser.setRoles(Set.of("USER"));
        mockUser.setFullName("Test User");
    }

    @Test
    void authenticate_ValidUser_ReturnsToken() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password123");
        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(mockUser));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response.getToken());
        assertTrue(response.isAuthenticated());
        assertEquals("Test User", response.getFullName());
        verify(userRepository, times(1)).findByuserName("testuser");
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "wrongpassword");
        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(mockUser));

        assertThrows(AppException.class, () -> authenticationService.authenticate(request), ErrorCode.UNAUTHENTICATED.name());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent", "password123");
        when(userRepository.findByuserName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> authenticationService.authenticate(request), ErrorCode.USER_NOT_EXITED.name());
    }

    @Test
    void introspect_ValidToken_ReturnsValidResponse() throws JOSEException, ParseException {
        String token = authenticationService.authenticate(new AuthenticationRequest("testuser", "password123")).getToken();
        IntrospectRequest request = new IntrospectRequest(token);

        IntrospectResponse response = authenticationService.introspect(request);

        assertTrue(response.isValid());
    }

    @Test
    void introspect_InvalidToken_ReturnsInvalidResponse() throws ParseException, JOSEException {
        String invalidToken = "invalid.token.string";
        IntrospectRequest request = new IntrospectRequest(invalidToken);

        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());
    }
}
