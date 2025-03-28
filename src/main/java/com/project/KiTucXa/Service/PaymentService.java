package com.project.KiTucXa.Service;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private PaymentMapper paymentMapper;
    public PaymentResponse createPayment(PaymentDto paymentDto) {
        Bill bill = billRepository.findById(paymentDto.getBillId())
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_FOUND));
// Kiểm tra số lần thanh toán đã tồn tại cho billId
        long paymentCount = paymentRepository.countByBill_BillId(paymentDto.getBillId());
        if (paymentCount >= 3) {
            throw new AppException(ErrorCode.PAYMENT_LIMIT_EXCEEDED);
        }
        Payment payment = paymentMapper.toPayment(paymentDto, bill);
        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    public List<PaymentResponse> getAllPayment() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentMapper::toPaymentResponse)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }


    public PaymentResponse updatePayment(String paymentId, PaymentUpdateDto paymentUpdateDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        paymentMapper.updatePayment(payment, paymentUpdateDto);
        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment);
    }

    public void deletePayment(String paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepository.deleteById(paymentId);
    }
}
