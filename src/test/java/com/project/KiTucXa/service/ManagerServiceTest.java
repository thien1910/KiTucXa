package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.ManagerDto;
import com.project.KiTucXa.Dto.Response.ManagerResponse;
import com.project.KiTucXa.Dto.Update.ManagerUpdateDto;
import com.project.KiTucXa.Entity.Manager;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.ManagerMapper;
import com.project.KiTucXa.Repository.ManagerRepository;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ManagerMapper managerMapper;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private User user;
    private ManagerDto managerDto;
    private ManagerResponse managerResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user-123");

        manager = new Manager();
        manager.setManagerId("manager-123");
        manager.setUser(user);
        manager.setDepartment("IT");

        managerDto = new ManagerDto("user-123", "IT");
        managerResponse = new ManagerResponse();

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(managerMapper.toManager(managerDto, user)).thenReturn(manager);
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);
    }

    @Test
    void testCreateManager_Success() {
        ManagerResponse result = managerService.createManager(managerDto);

        assertNotNull(result, "ManagerResponse không được null");
        verify(managerRepository).save(manager);
        verify(managerMapper).toManagerResponse(manager, user);
    }

    @Test
    void testCreateManager_UserNotFound() {
        when(userRepository.findById("user-123")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> managerService.createManager(managerDto));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void testGetAllManagers_Success() {
        when(managerRepository.findAll()).thenReturn(List.of(manager));
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);

        List<ManagerResponse> result = managerService.getAllManagers();

        assertEquals(1, result.size());
        verify(managerRepository).findAll();
    }

    @Test
    void testGetManager_Success() {
        when(managerRepository.findById("manager-123")).thenReturn(Optional.of(manager));

        ManagerResponse result = managerService.getManager("manager-123");

        assertNotNull(result);
        verify(managerRepository).findById("manager-123");
    }

    @Test
    void testGetManager_NotFound() {
        when(managerRepository.findById("invalid-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> managerService.getManager("invalid-id"));

        assertEquals("Manager not found", exception.getMessage());
    }

    @Test
    void testUpdateManager_Success() {
        ManagerUpdateDto updateDto = new ManagerUpdateDto("manager-123", "user-123", "HR");
        when(managerRepository.findById("manager-123")).thenReturn(Optional.of(manager));

        ManagerResponse result = managerService.updateManager("manager-123", updateDto);

        assertNotNull(result);
        verify(managerRepository).save(manager);
    }

    @Test
    void testDeleteManager_Success() {
        when(managerRepository.existsById("manager-123")).thenReturn(true);

        assertDoesNotThrow(() -> managerService.deleteManager("manager-123"));

        verify(managerRepository).deleteById("manager-123");
    }

    @Test
    void testDeleteManager_NotFound() {
        when(managerRepository.existsById("invalid-id")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> managerService.deleteManager("invalid-id"));

        assertEquals("Manager not found", exception.getMessage());
    }
}
