package com.project.KiTucXa.service;

import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Role;
import com.project.KiTucXa.Enum.Status;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User user;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("testuser")
                .passWord("password123")
                .status(Status.Staying)  // Đảm bảo rằng Status.Staying là giá trị hợp lệ trong enum Status
                .roles(Collections.singleton(Role.MANAGER.name())) // Chuyển đổi enum thành chuỗi
                .build();
    }



    @Test
    void testLoadUserByUsername_UserExists() {
        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByuserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown"));
    }
}