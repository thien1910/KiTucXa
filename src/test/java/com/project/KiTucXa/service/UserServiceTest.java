package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.project.KiTucXa.Dto.Request.UserCreationRequest;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.PasswordUpdateDto;
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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

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
    private UserCreationRequest userCreationRequest;
    private UserUpdateRequest userUpdateRequest;
    private PasswordUpdateDto passwordUpdateDto;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Khởi tạo user mẫu (giả sử user đã tồn tại trong DB)
        user = new User();
        user.setUserId("u123");
        user.setUserName("testuser");
        user.setPassWord("encodedOldPassword");
        user.setFullName("Test User");
        user.setGender(Gender.MALE);
        user.setStatus(Status.Staying);
        user.setRoles(new HashSet<>(List.of(Role.STUDENT.name())));

        // Khởi tạo đối tượng theo cách sử dụng setter
        userCreationRequest = new UserCreationRequest();
        userCreationRequest.setUserName("newuser");
        userCreationRequest.setPassWord("plainPassword");
        userCreationRequest.setFullName("New User");
        userCreationRequest.setGender(Gender.FEMALE);
        userCreationRequest.setCccd("123456789");
        userCreationRequest.setPhoneNumber("0123456789");
        userCreationRequest.setStatus(Status.Staying);
        userCreationRequest.setCountry("Vietnam");

        // Khởi tạo request cho update user
        userUpdateRequest = new UserUpdateRequest(
                "updatedPassword", "Updated Name", Gender.FEMALE,
                "NewRoom", "987654321", "0987654321",
                Status.Staying, "USA"
        );

        // Khởi tạo request cho update password
        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setOldPassword("oldPassword");
        passwordUpdateDto.setNewPassword("newPassword");

        // Khởi tạo response mẫu
        userResponse = new UserResponse();
        userResponse.setUserId("u123");
        userResponse.setUserName("testuser");
    }

    // ------------------ createUser ------------------
    @Test
    void testCreateUser_Success() {
        // Stub: user chưa tồn tại
        when(userRepository.existsByuserName("newuser")).thenReturn(false);
        // Chuyển đổi từ request sang entity
        when(userMapper.toUser(userCreationRequest)).thenReturn(user);
        // Mã hóa mật khẩu
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPlainPassword");
        // Lưu user
        when(userRepository.save(user)).thenReturn(user);
        // Chuyển đổi sang response
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userCreationRequest);

        assertNotNull(result);
        assertEquals("u123", result.getUserId());
        // Đảm bảo roles được gán (trong service gán STUDENT)
        assertTrue(user.getRoles().contains(Role.STUDENT.name()));
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Stub: nếu tồn tại user với userName "newuser"
        when(userRepository.existsByuserName(anyString())).thenReturn(true);

        AppException ex = assertThrows(AppException.class,
                () -> userService.createUser(userCreationRequest));
        assertEquals(ErrorCode.USER_EXITED, ex.getErrorCode());
    }

    // ------------------ updateUser ------------------
    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById("u123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser("u123", userUpdateRequest);

        assertNotNull(result);
        assertEquals("u123", result.getUserId());
        verify(userMapper).updateUser(user, userUpdateRequest);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById("u123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser("u123", userUpdateRequest));
        assertEquals("User not found", ex.getMessage());
    }

    // ------------------ deleteUser ------------------
    @Test
    void testDeleteUser() {
        // Vì deleteUser không kiểm tra tồn tại nên chỉ cần verify
        doNothing().when(userRepository).deleteById("u123");

        userService.deleteUser("u123");

        verify(userRepository).deleteById("u123");
    }

    // ------------------ getUsers ------------------
    @Test
    void testGetUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("u123", result.get(0).getUserId());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> result = userService.getUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    // ------------------ getUser ------------------
    @Test
    void testGetUser_Success() {
        when(userRepository.findById("u123")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUser("u123");

        assertNotNull(result);
        assertEquals("u123", result.getUserId());
        verify(userRepository).findById("u123");
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById("u123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUser("u123"));
        assertEquals("User not found", ex.getMessage());
    }

    // ------------------ getMyInfo ------------------
    @Test
    void testGetMyInfo_Success() {
        // Giả lập SecurityContext với username "testuser"
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getMyInfo();

        assertNotNull(result);
        assertEquals("u123", result.getUserId());
        verify(userRepository).findByuserName("testuser");
    }

    @Test
    void testGetMyInfo_UserNotFound() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.getMyInfo());
        assertEquals(ErrorCode.USER_NOT_EXITED, ex.getErrorCode());
    }

    // ------------------ changePassword ------------------
    @Test
    void testChangePassword_Success() {
        // Giả lập SecurityContext với username "testuser"
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(context);

        // Stub tìm user theo username
        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));
        // Giả lập khớp mật khẩu cũ
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        // Mã hóa mật khẩu mới
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // Thực hiện thay đổi mật khẩu
        assertDoesNotThrow(() -> userService.changePassword(passwordUpdateDto));
        // Kiểm tra mật khẩu đã được cập nhật
        assertEquals("encodedNewPassword", user.getPassWord());
    }

    @Test
    void testChangePassword_UserNotFound() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("unknownUser");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByuserName("unknownUser")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.changePassword(passwordUpdateDto));
        assertEquals(ErrorCode.USER_NOT_EXITED, ex.getErrorCode());
    }

    @Test
    void testChangePassword_OldPasswordMismatch() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));
        // Giả lập mật khẩu cũ không khớp
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(false);

        AppException ex = assertThrows(AppException.class,
                () -> userService.changePassword(passwordUpdateDto));
        assertEquals(ErrorCode.UNAUTHENTICATED, ex.getErrorCode());
    }
}
