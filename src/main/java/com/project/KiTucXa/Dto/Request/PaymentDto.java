package com.project.KiTucXa.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    @NotNull(message = "billId is required")
    String billId;
    BigDecimal installmentCount; // số lần đóng (tối đa ba lần)
}
