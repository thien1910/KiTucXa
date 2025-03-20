package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.PaymentDto;
import com.project.KiTucXa.Dto.Response.PaymentResponse;
import com.project.KiTucXa.Dto.Update.PaymentUpdateDto;
import com.project.KiTucXa.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/Payment")
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

    @PutMapping("/update/{paymentId}")
    public PaymentResponse updatePayment(
            @PathVariable("paymentId") String paymentId,
            @RequestBody PaymentUpdateDto paymentUpdateDto) {
        return paymentService.updatePayment(paymentId, paymentUpdateDto);
    }

    @DeleteMapping("/delete/{paymentId}")
    public String deletePayment(@PathVariable("paymentId") String paymentId) {
        paymentService.deletePayment(paymentId);
        return "Payment has been deleted";
    }
}
