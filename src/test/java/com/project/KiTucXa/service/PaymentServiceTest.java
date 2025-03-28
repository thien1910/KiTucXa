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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Bill bill;
    private Payment payment;
    private PaymentDto paymentDto;
    private PaymentResponse paymentResponse;
    private PaymentUpdateDto paymentUpdateDto;

    @BeforeEach
    void setUp() {
        // Khởi tạo bill
        bill = new Bill();
        bill.setBillId("bill1");

        // Khởi tạo Payment entity
        payment = new Payment();
        payment.setPaymentId("pmt1");
        payment.setBill(bill);
        // Giả sử installmentCount là số lần thanh toán được lưu dưới dạng BigDecimal
        payment.setInstallmentCount(BigDecimal.ONE);

        // Khởi tạo PaymentDto để tạo Payment (installmentCount có thể được truyền vào)
        paymentDto = new PaymentDto();
        paymentDto.setBillId("bill1");
        paymentDto.setInstallmentCount(BigDecimal.ONE);

        // Khởi tạo PaymentResponse (sẽ được mapper trả về)
        paymentResponse = PaymentResponse.builder()
                .paymentId("pmt1")
                .billId("bill1")
                .installmentCount(BigDecimal.ONE)
                .note("Success")
                .build();

        // Khởi tạo PaymentUpdateDto
        paymentUpdateDto = new PaymentUpdateDto();
        paymentUpdateDto.setInstallmentCount(BigDecimal.valueOf(2));
    }

    @Test
    void testCreatePayment_Success() {
        // Stub billRepository: tìm thấy bill
        when(billRepository.findById("bill1")).thenReturn(Optional.of(bill));
        // Stub paymentRepository.countByBill_BillId: trả về số lần thanh toán đã tồn tại < 3
        when(paymentRepository.countByBill_BillId("bill1")).thenReturn(1L);
        // Stub mapper: chuyển PaymentDto + bill thành Payment entity
        when(paymentMapper.toPayment(paymentDto, bill)).thenReturn(payment);
        // Stub save: trả về payment đã được lưu
        when(paymentRepository.save(payment)).thenReturn(payment);
        // Stub mapper: chuyển Payment entity thành PaymentResponse
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(paymentDto);

        assertNotNull(result);
        assertEquals("pmt1", result.getPaymentId());
        assertEquals("bill1", result.getBillId());
        assertEquals(BigDecimal.ONE, result.getInstallmentCount());
        // Verify các repository được gọi
        verify(billRepository, times(1)).findById("bill1");
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testCreatePayment_BillNotFound() {
        when(billRepository.findById("bill1")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            paymentService.createPayment(paymentDto);
        });

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreatePayment_PaymentLimitExceeded() {
        when(billRepository.findById("bill1")).thenReturn(Optional.of(bill));
        // Stub số lần thanh toán >= 3
        when(paymentRepository.countByBill_BillId("bill1")).thenReturn(3L);

        AppException exception = assertThrows(AppException.class, () -> {
            paymentService.createPayment(paymentDto);
        });

        assertEquals(ErrorCode.PAYMENT_LIMIT_EXCEEDED, exception.getErrorCode());
    }

    @Test
    void testGetAllPayment() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);

        List<PaymentResponse> result = paymentService.getAllPayment();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetPayment_Success() {
        when(paymentRepository.findById("pmt1")).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.getPayment("pmt1");

        assertNotNull(result);
        assertEquals("pmt1", result.getPaymentId());
    }

    @Test
    void testGetPayment_NotFound() {
        when(paymentRepository.findById("pmt1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPayment("pmt1");
        });

        assertEquals("Payment not found", exception.getMessage());
    }

    @Test
    void testUpdatePayment_Success() {
        when(paymentRepository.findById("pmt1")).thenReturn(Optional.of(payment));
        // Giả sử mapper updatePayment sẽ cập nhật installmentCount
        doAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            PaymentUpdateDto dto = invocation.getArgument(1);
            p.setInstallmentCount(dto.getInstallmentCount());
            return null;
        }).when(paymentMapper).updatePayment(any(Payment.class), any(PaymentUpdateDto.class));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(
                PaymentResponse.builder()
                        .paymentId("pmt1")
                        .billId("bill1")
                        .installmentCount(payment.getInstallmentCount())
                        .build()
        );

        PaymentResponse result = paymentService.updatePayment("pmt1", paymentUpdateDto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1), result.getInstallmentCount());
    }

    @Test
    void testDeletePayment_Success() {
        when(paymentRepository.existsById("pmt1")).thenReturn(true);
        doNothing().when(paymentRepository).deleteById("pmt1");

        paymentService.deletePayment("pmt1");

        verify(paymentRepository, times(1)).deleteById("pmt1");
    }

    @Test
    void testDeletePayment_NotFound() {
        when(paymentRepository.existsById("pmt1")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment("pmt1");
        });

        assertEquals("Payment not found", exception.getMessage());
    }
}

