package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.request.PaymentDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.response.PaymentResponse;
import com.project.KiTucXa.dto.update.PaymentUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.entity.Manager;
import com.project.KiTucXa.entity.Payment;
import com.project.KiTucXa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface PaymentMapper {
    // Chuyển từ PaymentDto sang Payment entity (có Bill truyền vào)
    @Mapping(target = "bill", source = "bill")
    Payment toPayment(PaymentDto paymentDto, Bill bill);

    // Chuyển từ entity sang response DTO
    @Mapping(source = "bill.billId", target = "billId")
    PaymentResponse toPaymentResponse(Payment payment);

    // Cập nhật Payment từ PaymentUpdateDto
    @Mapping(source = "paymentDto.note", target = "note")
    void updatePayment(@MappingTarget Payment payment, PaymentUpdateDto paymentDto);

}
