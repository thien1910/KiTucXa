package com.project.KiTucXa.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.project.KiTucXa.Dto.Request.AuthenticationRequest;
import com.project.KiTucXa.Dto.Request.IntrospectRequest;
import com.project.KiTucXa.Dto.Response.AuthenticationResponse;
import com.project.KiTucXa.Dto.Response.IntrospectResponse;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Status;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private User mockUser;
    private Object logger;

    @BeforeEach
    void setUp() {
        authenticationService.SIGNER_KEY = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij"; // Đặt khóa giả lập

        mockUser = new User();
        mockUser.setUserId("123");
        mockUser.setUserName("testuser");
        mockUser.setPassWord(passwordEncoder.encode("password123"));
        mockUser.setStatus(Status.Staying);
        mockUser.setRoles(Set.of("MANAGER"));
        mockUser.setFullName("Test User");
    }

    @Test
    void testAuthenticate_ValidUser_ShouldReturnToken() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password123");

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(mockUser));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response.getToken());
        assertTrue(response.isAuthenticated());
        assertEquals("Test User", response.getFullName());
        assertEquals("123", response.getUserId());
        assertEquals(Status.Staying, response.getStatus());
        assertTrue(response.getRoles().contains("MANAGER"));
    }

    @Test
    void testAuthenticate_InvalidPassword_ShouldThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "wrongpassword");

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(mockUser));

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void testAuthenticate_NonExistentUser_ShouldThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent", "password");

        when(userRepository.findByuserName("nonexistent")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.USER_NOT_EXITED, exception.getErrorCode());
    }

    @Test
    void testAuthenticate_DisciplinedUser_ShouldThrowException() {
        mockUser.setStatus(Status.Disciplined);
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password123");

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(mockUser));

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.USER_FORBIDDENED, exception.getErrorCode());
    }

    @Test
    void testIntrospect_ValidToken_ShouldReturnValidTrue() throws JOSEException, ParseException {
        String token = authenticationService.generateToken(mockUser);
        IntrospectRequest request = new IntrospectRequest(token);

        IntrospectResponse response = authenticationService.introspect(request);

        assertTrue(response.isValid());
    }

    @Test
    void testIntrospect_InvalidToken_ShouldReturnValidFalse() throws JOSEException, ParseException {
        IntrospectRequest request = new IntrospectRequest("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZXZ0ZXJpYS5jb20iLCJzdWIiOiJNQU5BR0VSIiwiZXhwIjoxNzQzMTUyOTIxLCJpYXQiOjE3NDMxNDkzMjEsInNjb3BlIjoiTUFOQUdFUiJ9.RZCKAOvPzjxJb7zDghlqlt6C-V55tMXMA4HkLNOUR1FC2Qh9mQr162L6oaBPYluexhtEka1mntAGG-yLzCd1F4");

        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());

        // Kiểm tra log có ghi nhận lỗi không
    }

}
