package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.UserCreationRequest;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.UserUpdateRequest;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Role;
import com.project.KiTucXa.Enum.Status;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.UserMapper;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    private User user;
    private UserResponse userResponse;
    private UserCreationRequest userCreationRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("123");
        user.setUserName("testuser");
        user.setPassWord("password");
        user.setFullName("Test User");
        user.setGender(Gender.MALE);
        user.setRoomNameStudent("Room A");
        user.setCccd("123456789");
        user.setPhoneNumber("0123456789");
        user.setStatus(Status.Staying);
        user.setCountry("Vietnam");
        user.setRoles(new HashSet<>(List.of(Role.STUDENT.name())));

        userResponse = new UserResponse();
        userResponse.setUserId("123");
        userResponse.setUserName("testuser");

        userCreationRequest = new UserCreationRequest
                ("testuser", "password", "Test User",
                        Gender.MALE, "Room A", "123456789",
                        "0123456789", Status.Staying, "Vietnam");

        userUpdateRequest = new UserUpdateRequest
                ("newpassword", "Updated User",
                        Gender.FEMALE, "Room B", "987654321",
                        "0987654321", Status.Staying, "USA");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByuserName(any())).thenReturn(false);
        when(userMapper.toUser(any())).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.createUser(userCreationRequest);

        assertNotNull(result);
        assertEquals("123", result.getUserId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        when(userRepository.existsByuserName(any())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, ()
                -> userService.createUser(userCreationRequest));
        assertEquals(ErrorCode.USER_EXITED, exception.getErrorCode());
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(user);

        UserResponse result = userService.updateUser("123", userUpdateRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()
                -> userService.updateUser("123", userUpdateRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(any());

        userService.deleteUser("123");

        verify(userRepository, times(1)).deleteById(any());
    }

    @Test
    void testGetUser_Success() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.getUser("123");

        assertNotNull(result);
        assertEquals("123", result.getUserId());
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()
                -> userService.getUser("123"));
        assertEquals("User not found", exception.getMessage());
    }
}
