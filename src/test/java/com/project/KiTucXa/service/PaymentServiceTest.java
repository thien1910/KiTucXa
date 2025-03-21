package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.PaymentDto;
import com.project.KiTucXa.Dto.Response.PaymentResponse;
import com.project.KiTucXa.Dto.Update.PaymentUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.Payment;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.PaymentMapper;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.PaymentRepository;
import com.project.KiTucXa.Service.PaymentService;
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
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private Bill bill;
    private PaymentDto paymentDto;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        bill = new Bill(); // Giả lập Bill
        bill.setBillId("bill-123");

        payment = new Payment();
        payment.setPaymentId("payment-123");
        payment.setBill(bill);

        paymentDto = new PaymentDto("bill-123", 2);
        paymentResponse = new PaymentResponse();

        when(billRepository.findById("bill-123")).thenReturn(Optional.of(bill));
        when(paymentMapper.toPayment(paymentDto, bill)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);
    }

    @Test
    void testCreatePayment_Success() {
        PaymentResponse result = paymentService.createPayment(paymentDto);

        assertNotNull(result, "PaymentResponse không được null");
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void testCreatePayment_BillNotFound() {
        when(billRepository.findById("bill-123")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> paymentService.createPayment(paymentDto));

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testGetAllPayment_Success() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);

        List<PaymentResponse> result = paymentService.getAllPayment();

        assertEquals(1, result.size());
        verify(paymentRepository).findAll();
    }

    @Test
    void testGetPayment_Success() {
        when(paymentRepository.findById("payment-123")).thenReturn(Optional.of(payment));

        PaymentResponse result = paymentService.getPayment("payment-123");

        assertNotNull(result);
        verify(paymentRepository).findById("payment-123");
    }

    @Test
    void testGetPayment_NotFound() {
        when(paymentRepository.findById("invalid-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.getPayment("invalid-id"));

        assertEquals("Payment not found", exception.getMessage());
    }

    @Test
    void testUpdatePayment_Success() {
        PaymentUpdateDto updateDto = new PaymentUpdateDto("Updated Note");
        when(paymentRepository.findById("payment-123")).thenReturn(Optional.of(payment));

        PaymentResponse result = paymentService.updatePayment("payment-123", updateDto);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
    }

    @Test
    void testDeletePayment_Success() {
        when(paymentRepository.existsById("payment-123")).thenReturn(true);

        assertDoesNotThrow(() -> paymentService.deletePayment("payment-123"));

        verify(paymentRepository).deleteById("payment-123");
    }

    @Test
    void testDeletePayment_NotFound() {
        when(paymentRepository.existsById("invalid-id")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.deletePayment("invalid-id"));

        assertEquals("Payment not found", exception.getMessage());
    }
}
