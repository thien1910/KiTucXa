package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.mapper.UserMapper;
import com.project.KiTucXa.repository.UserRepository;
import com.project.KiTucXa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUserName("testUser");
        userDto.setPassWord("password123");

        user = new User();
        user.setUserName("testUser");

        userResponse = new UserResponse();
        userResponse.setUserName("testUser");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByuserName(userDto.getUserName())).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertEquals("testUser", createdUser.getUserName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        when(userRepository.existsByuserName(userDto.getUserName())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse foundUser = userService.getUser("1");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUserName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser("1"));
    }

    @Test
    void testUpdateUser_Success() {
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(user, updateDto);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse updatedUser = userService.updateUser("1", updateDto);

        assertNotNull(updatedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById("1");

        userService.deleteUser("1");

        verify(userRepository, times(1)).deleteById("1");
    }
}
