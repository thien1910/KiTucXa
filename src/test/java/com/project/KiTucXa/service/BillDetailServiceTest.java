package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.BillDetailDto;
import com.project.KiTucXa.Dto.Response.BillDetailResponse;
import com.project.KiTucXa.Dto.Update.BillDetailUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.BillDetail;
import com.project.KiTucXa.Entity.UtilityService;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.BillDetailMapper;
import com.project.KiTucXa.Repository.BillDetailRepository;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.UtilityServiceRepository;
import com.project.KiTucXa.Service.BillDetailService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillDetailServiceTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private UtilityServiceRepository utilityServiceRepository;

    @Mock
    private BillDetailMapper billDetailMapper;

    @InjectMocks
    private BillDetailService billDetailService;

    private Bill bill;
    private UtilityService utilityService;
    private BillDetail billDetail;
    private BillDetailDto billDetailDto;
    private BillDetailResponse billDetailResponse;

    @BeforeEach
    void setUp() {
        bill = new Bill();
        bill.setBillId("bill-001");

        utilityService = new UtilityService();
        utilityService.setUtilityServiceId("service-001");
        utilityService.setPricePerUnit(new BigDecimal("5000"));

        billDetail = new BillDetail();
        billDetail.setBillDetailId("billDetail-001");
        billDetail.setBill(bill);
        billDetail.setUtilityService(utilityService);
        billDetail.setQuantity(10);
        billDetail.setTotalPrice(new BigDecimal("50000"));

        billDetailDto = new BillDetailDto("bill-001", "service-001", 10, new BigDecimal("50000"));
        billDetailResponse = new BillDetailResponse();

        when(billRepository.findById("bill-001")).thenReturn(Optional.of(bill));
        when(utilityServiceRepository.findById("service-001")).thenReturn(Optional.of(utilityService));
        when(billDetailMapper.toBillDetail(billDetailDto)).thenReturn(billDetail);
        when(billDetailRepository.save(billDetail)).thenReturn(billDetail);
        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);
    }

    @Test
    void testCreateBillDetail_Success() {
        BillDetailResponse result = billDetailService.createBillDetail(billDetailDto);

        assertNotNull(result, "BillDetailResponse không được null");
        verify(billDetailRepository).save(billDetail);
        verify(billDetailMapper).toBillDetailResponse(billDetail);
    }

    @Test
    void testCreateBillDetail_BillNotFound() {
        when(billRepository.findById("bill-001")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> billDetailService.createBillDetail(billDetailDto));

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
        verify(billDetailRepository, never()).save(any());
    }

    @Test
    void testCreateBillDetail_UtilityServiceNotFound() {
        when(utilityServiceRepository.findById("service-001")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> billDetailService.createBillDetail(billDetailDto));

        assertEquals(ErrorCode.UTILITY_SERVICE_NOT_FOUND, exception.getErrorCode());
        verify(billDetailRepository, never()).save(any());
    }

    @Test
    void testGetAllBillDetails_Success() {
        when(billDetailRepository.findAll()).thenReturn(List.of(billDetail));
        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);

        List<BillDetailResponse> result = billDetailService.getAllBillDetails();

        assertEquals(1, result.size());
        verify(billDetailRepository).findAll();
    }

    @Test
    void testGetBillDetailById_Success() {
        when(billDetailRepository.findById("billDetail-001")).thenReturn(Optional.of(billDetail));

        BillDetailResponse result = billDetailService.getBillDetailById("billDetail-001");

        assertNotNull(result);
        verify(billDetailRepository).findById("billDetail-001");
    }

    @Test
    void testGetBillDetailById_NotFound() {
        when(billDetailRepository.findById("invalid-id")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> billDetailService.getBillDetailById("invalid-id"));

        assertEquals(ErrorCode.BILL_DETAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateBillDetail_Success() {
        BillDetailUpdateDto updateDto = new BillDetailUpdateDto("billDetail-001", "bill-001",
                "service-001", 15, new BigDecimal("75000"));

        when(billDetailRepository.findById("billDetail-001")).thenReturn(Optional.of(billDetail));

        BillDetailResponse result = billDetailService.updateBillDetail("billDetail-001", updateDto);

        assertNotNull(result);
        verify(billDetailRepository).save(billDetail);
    }

    @Test
    void testDeleteBillDetail_Success() {
        when(billDetailRepository.existsById("billDetail-001")).thenReturn(true);

        assertDoesNotThrow(() -> billDetailService.deleteBillDetail("billDetail-001"));

        verify(billDetailRepository).deleteById("billDetail-001");
    }

    @Test
    void testDeleteBillDetail_NotFound() {
        when(billDetailRepository.existsById("invalid-id")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> billDetailService.deleteBillDetail("invalid-id"));

        assertEquals(ErrorCode.BILL_DETAIL_NOT_FOUND, exception.getErrorCode());
    }
}
