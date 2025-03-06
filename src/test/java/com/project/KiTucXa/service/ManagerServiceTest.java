package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.update.ManagerUpdateDto;
import com.project.KiTucXa.entity.Manager;
import com.project.KiTucXa.mapper.ManagerMapper;
import com.project.KiTucXa.repository.ManagerRepository;
import com.project.KiTucXa.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ManagerMapper managerMapper;

    @InjectMocks
    private ManagerService managerService;

    private Manager manager;
    private ManagerDto managerDto;
    private ManagerResponse managerResponse;

    @BeforeEach
    void setUp() {
        manager = new Manager();
        managerDto = new ManagerDto();
        managerResponse = new ManagerResponse();
    }

    @Test
    void getAllManager_ShouldReturnList() {
        when(managerRepository.findAll()).thenReturn(Arrays.asList(manager));
        when(managerMapper.toManagerResponse(any())).thenReturn(managerResponse);

        List<ManagerResponse> result = managerService.getAllManager();

        assertEquals(1, result.size());
        verify(managerRepository, times(1)).findAll();
    }

    @Test
    void getManager_WhenManagerExists_ShouldReturnManager() {
        when(managerRepository.findById("1")).thenReturn(Optional.of(manager));
        when(managerMapper.toManagerResponse(manager)).thenReturn(managerResponse);

        ManagerResponse result = managerService.getManager("1");

        assertNotNull(result);
        verify(managerRepository, times(1)).findById("1");
    }

    @Test
    void getManager_WhenManagerNotExists_ShouldThrowException() {
        when(managerRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> managerService.getManager("1"));
    }

    @Test
    void createManager_ShouldReturnSavedManager() {
        when(managerMapper.toManager(managerDto)).thenReturn(manager);
        when(managerRepository.save(manager)).thenReturn(manager);

        Manager result = managerService.createManager(managerDto);

        assertNotNull(result);
        verify(managerRepository, times(1)).save(manager);
    }

    @Test
    void updateManager_WhenManagerExists_ShouldReturnUpdatedManager() {
        ManagerUpdateDto updateDto = new ManagerUpdateDto();

        when(managerRepository.findById("1")).thenReturn(Optional.of(manager));
        doNothing().when(managerMapper).updateManager(manager, updateDto);
        when(managerRepository.save(manager)).thenReturn(manager);
        when(managerMapper.toManagerResponse(manager)).thenReturn(managerResponse);

        ManagerResponse result = managerService.updateManager("1", updateDto);

        assertNotNull(result);
        verify(managerRepository, times(1)).save(manager);
    }

    @Test
    void deleteManager_WhenManagerExists_ShouldDeleteManager() {
        when(managerRepository.existsById("1")).thenReturn(true);
        doNothing().when(managerRepository).deleteById("1");

        assertDoesNotThrow(() -> managerService.deleteManager("1"));
        verify(managerRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteManager_WhenManagerNotExists_ShouldThrowException() {
        when(managerRepository.existsById("1")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> managerService.deleteManager("1"));
    }
}