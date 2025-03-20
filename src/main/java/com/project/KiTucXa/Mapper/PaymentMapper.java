package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.PaymentDto;
import com.project.KiTucXa.Dto.Response.PaymentResponse;
import com.project.KiTucXa.Dto.Update.PaymentUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.Payment;
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
