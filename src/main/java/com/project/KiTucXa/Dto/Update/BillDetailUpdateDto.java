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
    String billDetailId;
    String billId;
    String utilityServiceId;
    Integer quantity;
    BigDecimal totalPrice;

}
