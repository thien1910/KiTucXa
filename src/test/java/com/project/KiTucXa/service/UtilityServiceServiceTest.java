package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.UtilityServiceDto;
import com.project.KiTucXa.dto.response.UtilityServiceResponse;
import com.project.KiTucXa.dto.update.UtilityServiceUpdateDto;
import com.project.KiTucXa.entity.UtilityService;
import com.project.KiTucXa.mapper.UtilityServiceMapper;
import com.project.KiTucXa.repository.UtilityServiceRepository;
import com.project.KiTucXa.service.UtilityServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UtilityServiceServiceTest {

    @Mock
    private UtilityServiceRepository utilityServiceRepository;

    @Mock
    private UtilityServiceMapper utilityServiceMapper;

    @InjectMocks
    private UtilityServiceService utilityServiceService;

    private UtilityService utilityService;
    private UtilityServiceDto utilityServiceDto;
    private UtilityServiceResponse utilityServiceResponse;

    @BeforeEach
    void setUp() {
        utilityService = new UtilityService();
        utilityServiceDto = new UtilityServiceDto();
        utilityServiceResponse = new UtilityServiceResponse();
    }

    @Test
    void getAllUtilityService_ShouldReturnList() {
        when(utilityServiceRepository.findAll()).thenReturn(Arrays.asList(utilityService));
        when(utilityServiceMapper.toUtilityServiceResponse(any())).thenReturn(utilityServiceResponse);

        List<UtilityServiceResponse> result = utilityServiceService.getAllUtilityService();

        assertEquals(1, result.size());
        verify(utilityServiceRepository, times(1)).findAll();
    }

    @Test
    void getUtilityService_WhenServiceExists_ShouldReturnService() {
        when(utilityServiceRepository.findById(1)).thenReturn(Optional.of(utilityService));
        when(utilityServiceMapper.toUtilityServiceResponse(utilityService)).thenReturn(utilityServiceResponse);

        UtilityServiceResponse result = utilityServiceService.getUtilityService(1);

        assertNotNull(result);
        verify(utilityServiceRepository, times(1)).findById(1);
    }

    @Test
    void getUtilityService_WhenServiceNotExists_ShouldThrowException() {
        when(utilityServiceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> utilityServiceService.getUtilityService(1));
    }

    @Test
    void createUtilityService_ShouldReturnSavedService() {
        when(utilityServiceMapper.toUtilityService(utilityServiceDto)).thenReturn(utilityService);
        when(utilityServiceRepository.save(utilityService)).thenReturn(utilityService);

        UtilityService result = utilityServiceService.createUtilityService(utilityServiceDto);

        assertNotNull(result);
        verify(utilityServiceRepository, times(1)).save(utilityService);
    }

    @Test
    void updateUtilityService_WhenServiceExists_ShouldReturnUpdatedService() {
        UtilityServiceUpdateDto updateDto = new UtilityServiceUpdateDto();

        when(utilityServiceRepository.findById(1)).thenReturn(Optional.of(utilityService));
        doNothing().when(utilityServiceMapper).updateService(utilityService, updateDto);
        when(utilityServiceRepository.save(utilityService)).thenReturn(utilityService);
        when(utilityServiceMapper.toUtilityServiceResponse(utilityService)).thenReturn(utilityServiceResponse);

        UtilityServiceResponse result = utilityServiceService.updateUtilityService(1, updateDto);

        assertNotNull(result);
        verify(utilityServiceRepository, times(1)).save(utilityService);
    }

    @Test
    void deleteUtilityService_WhenServiceExists_ShouldDeleteService() {
        when(utilityServiceRepository.existsById(1)).thenReturn(true);
        doNothing().when(utilityServiceRepository).deleteById(1);

        assertDoesNotThrow(() -> utilityServiceService.deleteUtilityService(1));
        verify(utilityServiceRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteUtilityService_WhenServiceNotExists_ShouldThrowException() {
        when(utilityServiceRepository.existsById(1)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> utilityServiceService.deleteUtilityService(1));
    }
}