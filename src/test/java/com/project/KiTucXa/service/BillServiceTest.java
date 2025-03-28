package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Mapper.BillMapper;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.ContractRepository;
import com.project.KiTucXa.Service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private BillMapper billMapper;

    @InjectMocks
    private BillService billService;

    private Bill bill;
    private BillDto billDto;
    private Contract contract;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Nguyen Van A");

        contract = new Contract();
        contract.setContractId("contract123");
        contract.setUser(user);
        contract.setContractStatus(ContractStatus.Active);

        billDto = new BillDto("contract123", new BigDecimal("100000"), new Date(System.currentTimeMillis()), PaymentMethod.BANK_TRANSFER, BillStatus.UNPAID, "Test Note");

        bill = new Bill();
        bill.setContract(contract);
        bill.setSumPrice(new BigDecimal("100000"));
        bill.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        bill.setBillStatus(BillStatus.UNPAID);
        bill.setNote("Test Note");
    }

    @Test
    void testCreateBill_Success() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        when(billMapper.toBill(billDto)).thenReturn(bill);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        BillResponse response = billService.createBill(billDto);

        assertNotNull(response);
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testCreateBill_ContractNotFound() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> billService.createBill(billDto));
    }

    @Test
    void testGetAllBills() {
        when(billRepository.findAll()).thenReturn(Collections.singletonList(bill));
        when(billMapper.toBillResponse(any())).thenReturn(new BillResponse());

        List<BillResponse> responses = billService.getAllBills();

        assertFalse(responses.isEmpty());
        verify(billRepository, times(1)).findAll();
    }

    @Test
    void testGetBillById_Success() {
        when(billRepository.findById("bill123")).thenReturn(Optional.of(bill));
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        BillResponse response = billService.getBillById("bill123");

        assertNotNull(response);
        verify(billRepository, times(1)).findById("bill123");
    }

    @Test
    void testGetBillById_NotFound() {
        when(billRepository.findById("bill123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> billService.getBillById("bill123"));
    }

    @Test
    void testUpdateBill_Success() {
        BillUpdateDto billUpdateDto = new BillUpdateDto(new BigDecimal("200000"), new Date(System.currentTimeMillis()), PaymentMethod.CASH, BillStatus.PAID, "Updated Note");
        when(billRepository.findById("bill123")).thenReturn(Optional.of(bill));
        doNothing().when(billMapper).updateBill(bill, billUpdateDto);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        BillResponse response = billService.updateBill("bill123", billUpdateDto);

        assertNotNull(response);
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testDeleteBill_Success() {
        when(billRepository.existsById("bill123")).thenReturn(true);
        doNothing().when(billRepository).deleteById("bill123");

        assertDoesNotThrow(() -> billService.deleteBill("bill123"));
        verify(billRepository, times(1)).deleteById("bill123");
    }

    @Test
    void testDeleteBill_NotFound() {
        when(billRepository.existsById("bill123")).thenReturn(false);

        assertThrows(AppException.class, () -> billService.deleteBill("bill123"));
    }
}
