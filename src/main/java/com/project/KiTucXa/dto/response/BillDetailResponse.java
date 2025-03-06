package com.project.KiTucXa.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillDetailResponse {
    String billDetailId;
    String billId;
    String utilityServiceId;
    Integer quantity;
    BigDecimal totalPrice;
}
