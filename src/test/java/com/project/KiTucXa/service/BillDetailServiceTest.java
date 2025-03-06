package com.project.KiTucXa.service;

import com.project.KiTucXa.dto.request.BillDetailDto;
import com.project.KiTucXa.dto.response.BillDetailResponse;
import com.project.KiTucXa.dto.update.BillDetailUpdateDto;
import com.project.KiTucXa.entity.BillDetail;
import com.project.KiTucXa.mapper.BillDetailMapper;
import com.project.KiTucXa.repository.BillDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillDetailServiceTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    @Mock
    private BillDetailMapper billDetailMapper;

    @InjectMocks
    private BillDetailService billDetailService;

    private BillDetail billDetail;
    private BillDetailDto billDetailDto;
    private BillDetailResponse billDetailResponse;
    private BillDetailUpdateDto billDetailUpdateDto;

//    @BeforeEach
//    void setUp() {
//        billDetail = BillDetail.builder()
//                .billDetailId(1)
//                .quantity(2)
//                .totalPrice(new BigDecimal("100.00"))
//                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
//                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
//                .build();
//
//        billDetailDto = new BillDetailDto(2, new BigDecimal("100.00"));
//        billDetailResponse = new BillDetailResponse(1, 2, new BigDecimal("100.00"));
//        billDetailUpdateDto = new BillDetailUpdateDto(3, new BigDecimal("150.00"));
//    }
//
//    @Test
//    void getAllBillDetail_ShouldReturnList() {
//        when(billDetailRepository.findAll()).thenReturn(Collections.singletonList(billDetail));
//        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);
//
//        List<BillDetailResponse> result = billDetailService.getAllBillDetail();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(billDetailResponse, result.get(0));
//    }
//
//    @Test
//    void getBillDetail_ShouldReturnBillDetailResponse_WhenFound() {
//        when(billDetailRepository.findById(1)).thenReturn(Optional.of(billDetail));
//        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);
//
//        BillDetailResponse result = billDetailService.getBillDetail(1);
//
//        assertNotNull(result);
//        assertEquals(billDetailResponse, result);
//    }
//
//    @Test
//    void getBillDetail_ShouldThrowException_WhenNotFound() {
//        when(billDetailRepository.findById(1)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> billDetailService.getBillDetail(1));
//        assertEquals("BillDetail not found", exception.getMessage());
//    }
//
//    @Test
//    void createBillDetail_ShouldReturnCreatedBillDetail() {
//        when(billDetailMapper.toBillDetail(billDetailDto)).thenReturn(billDetail);
//        when(billDetailRepository.save(billDetail)).thenReturn(billDetail);
//
//        BillDetail result = billDetailService.createBillDetail(billDetailDto);
//
//        assertNotNull(result);
//        assertEquals(billDetail, result);
//    }
//
//    @Test
//    void updateBillDetail_ShouldReturnUpdatedBillDetailResponse_WhenFound() {
//        when(billDetailRepository.findById(1)).thenReturn(Optional.of(billDetail));
//        doNothing().when(billDetailMapper).updateBillDetail(billDetail, billDetailUpdateDto);
//        when(billDetailRepository.save(billDetail)).thenReturn(billDetail);
//        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);
//
//        BillDetailResponse result = billDetailService.updateBillDetail(1, billDetailUpdateDto);
//
//        assertNotNull(result);
//        assertEquals(billDetailResponse, result);
//    }
//
//    @Test
//    void updateBillDetail_ShouldThrowException_WhenNotFound() {
//        when(billDetailRepository.findById(1)).thenReturn(Optional.empty());
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> billDetailService.updateBillDetail(1, billDetailUpdateDto));
//        assertEquals("BillDetail not found", exception.getMessage());
//    }
//
//    @Test
//    void deleteBillDetail_ShouldDeleteSuccessfully_WhenFound() {
//        when(billDetailRepository.existsById(1)).thenReturn(true);
//        doNothing().when(billDetailRepository).deleteById(1);
//
//        assertDoesNotThrow(() -> billDetailService.deleteBillDetail(1));
//    }
//
//    @Test
//    void deleteBillDetail_ShouldThrowException_WhenNotFound() {
//        when(billDetailRepository.existsById(1)).thenReturn(false);
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> billDetailService.deleteBillDetail(1));
//        assertEquals("BillDetail not found", exception.getMessage());
//    }
}
