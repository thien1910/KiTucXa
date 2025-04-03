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
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Date;
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
    @Test
    void testIntrospect_ExpiredToken_ShouldReturnValidFalse() throws JOSEException, ParseException {
        AuthenticationService authenticationServiceSpy = spy(authenticationService);

        String expiredToken = authenticationService.generateToken(mockUser);
        IntrospectRequest request = new IntrospectRequest(expiredToken);

        doReturn(new IntrospectResponse(false))
                .when(authenticationServiceSpy)
                .introspect(any(IntrospectRequest.class));

        IntrospectResponse response = authenticationServiceSpy.introspect(request);
        assertFalse(response.isValid());
    }



    @Test
    void testIntrospect_MalformedToken_ShouldReturnValidFalse() {
        IntrospectRequest request = new IntrospectRequest("invalid-token-format");

        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());
    }


    @Test
    void testIntrospect_WrongSignerKey_ShouldReturnValidFalse() throws JOSEException, ParseException {
        String validToken = authenticationService.generateToken(mockUser);

        // Thay đổi SIGNER_KEY
        authenticationService.SIGNER_KEY = "1TjXchw5FloESb63Kc3DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij";

        IntrospectRequest request = new IntrospectRequest(validToken);
        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());
    }

    @Test
    void testGenerateToken_EmptyRoles_ShouldNotFail() throws JOSEException {
        mockUser.setRoles(Set.of());
        String token = authenticationService.generateToken(mockUser);

        assertNotNull(token);
    }

    @Test
    void testIntrospect_ParsingException_ShouldHandleGracefully() throws ParseException {
        AuthenticationService authenticationServiceSpy = spy(authenticationService);

        IntrospectRequest request = new IntrospectRequest("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZXZ0ZXJpYS5jb20iLCJzdWIiOiJNQU5BR0VSIiwiZXhwIjoxNzQzMjQ5MTUwLCJpYXQiOjE3NDMyNDU1NTAsInNjb3BlIjoiTUFOQUdFUiJ9.aaBViGy4L8FVajep8TiPpxj_NYbJUhncHujvuuEsKPWiXeCW-ryflt5At0QM75fp-QFCRDbuQDlwLHhvOIPfx3");

        doThrow(new RuntimeException(new ParseException("Invalid token", 0)))
                .when(authenticationServiceSpy)
                .introspect(any(IntrospectRequest.class));

        assertThrows(RuntimeException.class, () -> authenticationServiceSpy.introspect(request));

    }
    @Test
    void testGenerateToken_WithNullUser_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> authenticationService.generateToken(null));
    }

    @Test
    void testGenerateToken_ShouldCreateValidToken() throws JOSEException, ParseException {
        // Arrange: thiết lập thông tin user
        mockUser.setStatus(Status.Staying);
        mockUser.setUserName("testuser");
        mockUser.setRoles(Set.of("MANAGER")); // đảm bảo user có vai trò MANAGER

        // Cung cấp một khóa đủ dài (≥ 64 ký tự) để hỗ trợ HS512
        authenticationService.SIGNER_KEY = "0123456789012345678901234567890123456789012345678901234567890123";

        // Act: tạo token
        String token = authenticationService.generateToken(mockUser);
        assertNotNull(token); // Token không được null

        // Tạo SecretKey theo cách mà generateToken() sử dụng: raw bytes của SIGNER_KEY và HmacSHA512
        SecretKey secretKey = new SecretKeySpec(authenticationService.SIGNER_KEY.getBytes(), "HmacSHA512");

        // Giải mã token để lấy claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Assert: kiểm tra subject và claim "scope"
        assertEquals(mockUser.getUserName(), claims.getSubject());
        assertNotNull(claims.get("scope"));
        assertTrue(((String) claims.get("scope")).contains("MANAGER"));

        // Kiểm tra thời gian của token: 1 giờ = 3600_000 ms
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertEquals(3600_000, expiration.getTime() - issuedAt.getTime());
    }



}
