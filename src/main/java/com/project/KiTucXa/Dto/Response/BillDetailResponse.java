package com.project.KiTucXa.Dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
    Timestamp createdAt;
    Timestamp updatedAt;

    public BillDetailResponse(String billDetail1, String bill1, String utility1, int i, BigDecimal bigDecimal) {
    }
}
