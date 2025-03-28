package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.KiTucXa.Dto.Request.ManagerDto;
import com.project.KiTucXa.Dto.Response.ManagerResponse;
import com.project.KiTucXa.Dto.Response.UserResponse;
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

@ExtendWith(MockitoExtension.class)
public class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ManagerMapper managerMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ManagerService managerService;

    // Các biến dùng chung cho test
    private Manager manager;
    private ManagerDto managerDto;
    private ManagerUpdateDto managerUpdateDto;
    private ManagerResponse managerResponse;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Giả lập đối tượng User
        user = new User();
        user.setUserId("user123");
        // Giả sử user có thêm thông tin khác nếu cần

        // Giả lập đối tượng Manager (Entity)
        manager = new Manager();
        manager.setManagerId("manager123");
        manager.setDepartment("IT");
        manager.setUser(user);

        // Giả lập DTO để tạo Manager
        managerDto = new ManagerDto("user123", "IT");

        // Giả lập DTO để update Manager (chỉ cập nhật department)
        managerUpdateDto = new ManagerUpdateDto("HR");

        // Giả lập response của Manager
        userResponse = new UserResponse();
        // Cấu hình các trường của UserResponse nếu cần

        managerResponse = ManagerResponse.builder()
                .managerId("manager123")
                .userId("user123")
                .department("IT")
                .user(userResponse)
                .build();
    }

    @Test
    void testGetAllManagers() {
        // Giả lập repository trả về 1 danh sách gồm 1 Manager
        when(managerRepository.findAll()).thenReturn(Arrays.asList(manager));
        // Giả lập mapper chuyển đối tượng sang response
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);

        List<ManagerResponse> responses = managerService.getAllManagers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(managerRepository).findAll();
    }

    @Test
    void testGetManager_Success() {
        when(managerRepository.findById("manager123")).thenReturn(Optional.of(manager));
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);

        ManagerResponse response = managerService.getManager("manager123");

        assertNotNull(response);
        assertEquals("manager123", response.getManagerId());
        verify(managerRepository).findById("manager123");
    }

    @Test
    void testGetManager_NotFound() {
        when(managerRepository.findById("manager123")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> managerService.getManager("manager123"));
        assertEquals(ErrorCode.MANAGER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateManager_Success() {
        // Giả lập rằng chưa có manager nào có department "IT"
        when(managerRepository.existsByDepartment("IT")).thenReturn(false);
        // Giả lập tìm kiếm user theo userId
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        // Chuyển đổi từ DTO sang entity
        when(managerMapper.toManager(managerDto, user)).thenReturn(manager);
        // Lưu entity vào repository
        when(managerRepository.save(manager)).thenReturn(manager);
        // Chuyển từ entity sang response
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);

        ManagerResponse response = managerService.createManager(managerDto);

        assertNotNull(response);
        assertEquals("manager123", response.getManagerId());
        verify(managerRepository).existsByDepartment("IT");
        verify(userRepository).findById("user123");
        verify(managerRepository).save(manager);
    }

    @Test
    void testCreateManager_ManagerAlreadyExisted() {
        // Nếu đã tồn tại manager với department tương ứng
        when(managerRepository.existsByDepartment("IT")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> managerService.createManager(managerDto));
        assertEquals(ErrorCode.MANAGER_EXITED, ex.getErrorCode());
    }

    @Test
    void testUpdateManager_Success() {
        when(managerRepository.findById("manager123")).thenReturn(Optional.of(manager));
        // Giả lập mapper updateManager (không trả về gì)
        doNothing().when(managerMapper).updateManager(manager, managerUpdateDto);
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toManagerResponse(manager, user)).thenReturn(managerResponse);

        ManagerResponse response = managerService.updateManager("manager123", managerUpdateDto);

        assertNotNull(response);
        assertEquals("manager123", response.getManagerId());
        verify(managerRepository).findById("manager123");
        verify(managerRepository).save(manager);
    }

    @Test
    void testUpdateManager_NotFound() {
        when(managerRepository.findById("manager123")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> managerService.updateManager("manager123", managerUpdateDto));
        assertEquals(ErrorCode.MANAGER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteManager_Success() {
        when(managerRepository.existsById("manager123")).thenReturn(true);
        doNothing().when(managerRepository).deleteById("manager123");

        assertDoesNotThrow(() -> managerService.deleteManager("manager123"));
        verify(managerRepository).deleteById("manager123");
    }

    @Test
    void testDeleteManager_NotFound() {
        when(managerRepository.existsById("manager123")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> managerService.deleteManager("manager123"));
        assertEquals(ErrorCode.MANAGER_NOT_FOUND, ex.getErrorCode());
    }
}
