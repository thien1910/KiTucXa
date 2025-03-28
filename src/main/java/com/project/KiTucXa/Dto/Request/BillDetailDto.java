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
@Getter
@Setter
public class BillDetailDto {
    @NotNull(message = "BillId is required")
    String billId;
    @NotNull(message = "utilityServiceId is required")
    String utilityServiceId;
    Integer quantity;
    BigDecimal totalPrice;

    public BillDetailDto(String bill1, String utility1, int i) {
    }
}