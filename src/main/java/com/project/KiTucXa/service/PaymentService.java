package com.project.KiTucXa.service;

import com.project.KiTucXa.dto.request.PaymentDto;
import com.project.KiTucXa.dto.response.PaymentResponse;
import com.project.KiTucXa.dto.update.PaymentUpdateDto;
import com.project.KiTucXa.entity.Payment;
import com.project.KiTucXa.mapper.PaymentMapper;
import com.project.KiTucXa.repository.PaymentRepository;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;
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

    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toPayment(paymentDto);
        return paymentRepository.save(payment);
    }


    public PaymentResponse updatePayment(String paymentId, PaymentUpdateDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentMapper.updatePayment(payment, paymentDto);
        payment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(payment);
    }

    public void deletePayment(String paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepository.deleteById(paymentId);
    }
}
