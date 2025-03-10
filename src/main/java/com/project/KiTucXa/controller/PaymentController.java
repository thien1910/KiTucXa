package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.PaymentDto;
import com.project.KiTucXa.dto.response.PaymentResponse;
import com.project.KiTucXa.dto.update.PaymentUpdateDto;
import com.project.KiTucXa.entity.Payment;
import com.project.KiTucXa.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/add")
    ApiResponse<PaymentResponse> createPayment(
            @RequestBody @Valid PaymentDto paymentDto) {
        ApiResponse<PaymentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(paymentService.createPayment(paymentDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<PaymentResponse> getAllPayment() {
        return paymentService.getAllPayment();
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(
            @PathVariable("paymentId") String paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PutMapping("/{paymentId}")
    public PaymentResponse updatePayment(
            @PathVariable("paymentId") String paymentId,
            @RequestBody PaymentUpdateDto paymentUpdateDto) {
        return paymentService.updatePayment(paymentId, paymentUpdateDto);
    }

    @DeleteMapping("/{paymentId}")
    public String deletePayment(@PathVariable("paymentId") String paymentId) {
        paymentService.deletePayment(paymentId);
        return "Payment has been deleted";
    }
}
