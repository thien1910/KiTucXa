package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.BillDto;
import com.project.KiTucXa.dto.response.BillResponse;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.entity.Contract;
import com.project.KiTucXa.mapper.BillMapper;
import com.project.KiTucXa.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillMapper billMapper;

    @InjectMocks
    private BillService billService;

    private Bill bill;
    private BillDto billDto;
    private BillResponse billResponse;
    private BillUpdateDto billUpdateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Contract contract = Contract.builder().contractId("contract123").build();

        bill = new Bill();
        bill.setBillId("1");
        bill.setContract(contract); // Đặt đối tượng Contract thay vì String
        bill.setSumPrice(BigDecimal.valueOf(500));

        billDto = new BillDto();
        billDto.setContract_id("contract123");
        billDto.setSumPrice(BigDecimal.valueOf(500));

        billResponse = new BillResponse();
        billResponse.setBillId("1");
        billResponse.setContractId("contract123");
        billResponse.setSumPrice(BigDecimal.valueOf(500));
    }

    @Test
    void getAllBills_ShouldReturnList() {
        when(billRepository.findAll()).thenReturn(List.of(bill));
        when(billMapper.toBillResponse(bill)).thenReturn(billResponse);

        List<BillResponse> bills = billService.getAllBill();
        assertEquals(1, bills.size());
        verify(billRepository, times(1)).findAll();
    }

    @Test
    void getBillById_ShouldReturnBillResponse() {
        when(billRepository.findById("1")).thenReturn(Optional.of(bill));
        when(billMapper.toBillResponse(bill)).thenReturn(billResponse);

        BillResponse response = billService.getBill("1");
        assertEquals("1", response.getBillId());
        verify(billRepository, times(1)).findById("1");
    }

    @Test
    void getBillById_ShouldThrowException_WhenNotFound() {
        when(billRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> billService.getBill("999"));
    }

    @Test
    void createBill_ShouldReturnCreatedBill() {
        when(billMapper.toBill(billDto)).thenReturn(bill);
        when(billRepository.save(bill)).thenReturn(bill);

        Bill created = billService.createBill(billDto);
        assertEquals(bill, created);
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void updateBill_ShouldReturnUpdatedBill() {
        BillUpdateDto updateDto = new BillUpdateDto();
        updateDto.setSumPrice(BigDecimal.valueOf(600));

        when(billRepository.findById("1")).thenReturn(Optional.of(bill));
        doNothing().when(billMapper).updateBill(bill, updateDto);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(billResponse);

        BillResponse updatedResponse = billService.updateBill("1", updateDto);
        assertEquals("1", updatedResponse.getBillId());
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void deleteBill_ShouldCallDelete() {
        when(billRepository.existsById("1")).thenReturn(true);
        doNothing().when(billRepository).deleteById("1");

        billService.deleteBill("1");
        verify(billRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteBill_ShouldThrowException_WhenNotFound() {
        when(billRepository.existsById("999")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> billService.deleteBill("999"));
    }
}
