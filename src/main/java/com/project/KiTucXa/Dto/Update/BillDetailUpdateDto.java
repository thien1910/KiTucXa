package com.project.KiTucXa.Dto.Update;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillDetailUpdateDto {
    Integer quantity;
    BigDecimal totalPrice;

    public BillDetailUpdateDto(int i) {
    }
}
