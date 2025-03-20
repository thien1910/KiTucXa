package com.project.KiTucXa.Dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String paymentId;
    String billId;
    int installmentCount; // số lần đóng (tối đa ba lần)
    String note;
    Timestamp createdAt;
    Timestamp updatedAt;
}
