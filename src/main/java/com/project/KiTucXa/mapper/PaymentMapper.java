package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.PaymentDto;
import com.project.KiTucXa.dto.response.PaymentResponse;
import com.project.KiTucXa.dto.update.PaymentUpdateDto;
import com.project.KiTucXa.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface PaymentMapper {
    Payment toPayment(PaymentDto paymentDto);
    PaymentResponse toPaymentResponse(Payment payment);
    void updatePayment(@MappingTarget Payment payment, PaymentUpdateDto paymentDto);
}
