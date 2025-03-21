package com.project.KiTucXa.service;

import com.google.zxing.WriterException;
import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private BillMapper billMapper;

    @InjectMocks
    private BillService billService;

    private Contract contract;
    private Bill bill;
    private BillDto billDto;
    private BillResponse billResponse;

    @BeforeEach
    void setUp() {
        contract = new Contract();
        contract.setContractId("contract-123");

        bill = new Bill();
        bill.setBillId("bill-789");
        bill.setContract(contract);
        bill.setSumPrice(new BigDecimal("1500000"));
        bill.setPaymentDate(Date.valueOf("2025-03-15"));
        bill.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        bill.setBillStatus(BillStatus.PAID);
        bill.setNote("Thanh toán tiền phòng tháng 3");

        billDto = new BillDto("contract-123", new BigDecimal("1500000"),
                Date.valueOf("2025-03-15"), PaymentMethod.BANK_TRANSFER, BillStatus.PAID, "Thanh toán tiền phòng tháng 3");

        billResponse = new BillResponse();

        when(contractRepository.findById("contract-123")).thenReturn(Optional.of(contract));
        when(billMapper.toBill(billDto)).thenReturn(bill);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(billResponse);
    }

    @Test
    void testCreateBill_Success() {
        BillResponse result = billService.createBill(billDto);

        assertNotNull(result, "BillResponse không được null");
        verify(billRepository).save(bill);
        verify(billMapper).toBillResponse(bill);
    }

    @Test
    void testCreateBill_ContractNotFound() {
        when(contractRepository.findById("contract-123")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> billService.createBill(billDto));

        assertEquals(ErrorCode.CONTRACT_NOT_FOUND, exception.getErrorCode());
        verify(billRepository, never()).save(any());
    }

    @Test
    void testGetAllBills_Success() {
        when(billRepository.findAll()).thenReturn(List.of(bill));
        when(billMapper.toBillResponse(bill)).thenReturn(billResponse);

        List<BillResponse> result = billService.getAllBills();

        assertEquals(1, result.size());
        verify(billRepository).findAll();
    }

    @Test
    void testGetBillById_Success() {
        when(billRepository.findById("bill-789")).thenReturn(Optional.of(bill));

        BillResponse result = billService.getBillById("bill-789");

        assertNotNull(result);
        verify(billRepository).findById("bill-789");
    }

    @Test
    void testGetBillById_NotFound() {
        when(billRepository.findById("invalid-id")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> billService.getBillById("invalid-id"));

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateBill_Success() {
        BillUpdateDto updateDto = new BillUpdateDto("bill-789", "contract-123",
                new BigDecimal("2000000"), Date.valueOf("2025-04-15"),
                PaymentMethod.CASH, BillStatus.UNPAID, "Cập nhật hóa đơn");

        when(billRepository.findById("bill-789")).thenReturn(Optional.of(bill));

        BillResponse result = billService.updateBill("bill-789", updateDto);

        assertNotNull(result);
        verify(billRepository).save(bill);
    }

    @Test
    void testDeleteBill_Success() {
        when(billRepository.existsById("bill-789")).thenReturn(true);

        assertDoesNotThrow(() -> billService.deleteBill("bill-789"));

        verify(billRepository).deleteById("bill-789");
    }

    @Test
    void testDeleteBill_NotFound() {
        when(billRepository.existsById("invalid-id")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> billService.deleteBill("invalid-id"));

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
    }
}
