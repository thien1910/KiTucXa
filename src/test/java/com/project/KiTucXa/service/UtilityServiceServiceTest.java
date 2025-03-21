package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.UtilityServiceDto;
import com.project.KiTucXa.Dto.Response.UtilityServiceResponse;
import com.project.KiTucXa.Dto.Update.UtilityServiceUpdateDto;
import com.project.KiTucXa.Entity.UtilityService;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.UtilityServiceMapper;
import com.project.KiTucXa.Repository.UtilityServiceRepository;
import com.project.KiTucXa.Service.UtilityServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilityServiceServiceTest {

    @Mock
    private UtilityServiceRepository utilityServiceRepository;

    @Mock
    private UtilityServiceMapper utilityServiceMapper;

    @InjectMocks
    private UtilityServiceService utilityServiceService;

    private UtilityService utilityService;
    private UtilityServiceResponse utilityServiceResponse;
    private UtilityServiceDto utilityServiceDto;
    private UtilityServiceUpdateDto utilityServiceUpdateDto;

    @BeforeEach
    void setUp() {
        utilityService = new UtilityService();
        utilityService.setUtilityServiceId("1");
        utilityService.setServiceName("Electricity");
        utilityService.setDescription("Monthly electricity bill");
        utilityService.setPricePerUnit(BigDecimal.valueOf(0.15));
        utilityService.setCalculationUnit("kWh");
        utilityService.setStatus(ContractStatus.Active);

        utilityServiceResponse = new UtilityServiceResponse();
        utilityServiceResponse.setUtilityServiceId("1");
        utilityServiceResponse.setServiceName("Electricity");

        utilityServiceDto = new UtilityServiceDto("Electricity", "Monthly electricity bill", BigDecimal.valueOf(0.15), "kWh", ContractStatus.Active);

        utilityServiceUpdateDto = new UtilityServiceUpdateDto("Water", "Monthly water bill", BigDecimal.valueOf(0.10), "m3", ContractStatus.Inactive);
    }

    @Test
    void testCreateUtilityService_Success() {
        when(utilityServiceMapper.toUtilityService(any())).thenReturn(utilityService);
        when(utilityServiceRepository.save(any())).thenReturn(utilityService);
        when(utilityServiceMapper.toUtilityServiceResponse(any())).thenReturn(utilityServiceResponse);

        UtilityServiceResponse result = utilityServiceService.createUtilityService(utilityServiceDto);

        assertNotNull(result);
        assertEquals("1", result.getUtilityServiceId());
        verify(utilityServiceRepository, times(1)).save(any());
    }

    @Test
    void testGetAllUtilityServices() {
        when(utilityServiceRepository.findAll()).thenReturn(List.of(utilityService));
        when(utilityServiceMapper.toUtilityServiceResponse(any())).thenReturn(utilityServiceResponse);

        List<UtilityServiceResponse> result = utilityServiceService.getAllUtilityServices();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetUtilityServiceById_Success() {
        when(utilityServiceRepository.findById(any())).thenReturn(Optional.of(utilityService));
        when(utilityServiceMapper.toUtilityServiceResponse(any())).thenReturn(utilityServiceResponse);

        UtilityServiceResponse result = utilityServiceService.getUtilityServiceById("1");

        assertNotNull(result);
        assertEquals("1", result.getUtilityServiceId());
    }

    @Test
    void testGetUtilityServiceById_NotFound() {
        when(utilityServiceRepository.findById(any())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> utilityServiceService.getUtilityServiceById("1"));
        assertEquals(ErrorCode.UTILITY_SERVICE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testDeleteUtilityService_Success() {
        when(utilityServiceRepository.existsById(any())).thenReturn(true);
        doNothing().when(utilityServiceRepository).deleteById(any());

        utilityServiceService.deleteUtilityService("1");

        verify(utilityServiceRepository, times(1)).deleteById(any());
    }

    @Test
    void testDeleteUtilityService_NotFound() {
        when(utilityServiceRepository.existsById(any())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> utilityServiceService.deleteUtilityService("1"));
        assertEquals(ErrorCode.UTILITY_SERVICE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateUtilityService_Success() {
        when(utilityServiceRepository.findById(any())).thenReturn(Optional.of(utilityService));
        when(utilityServiceRepository.save(any())).thenReturn(utilityService);
        when(utilityServiceMapper.toUtilityServiceResponse(any())).thenReturn(utilityServiceResponse);

        UtilityServiceResponse result = utilityServiceService.updateUtilityService("1", utilityServiceUpdateDto);

        assertNotNull(result);
        verify(utilityServiceRepository, times(1)).save(any());
    }

    @Test
    void testUpdateUtilityService_NotFound() {
        when(utilityServiceRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> utilityServiceService.updateUtilityService("1", utilityServiceUpdateDto));
        assertEquals("Service not found", exception.getMessage());
    }
}
