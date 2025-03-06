package com.project.KiTucXa.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String billId;
    int installmentCount; // số lần đóng (tối đa ba lần)
    String note;
}
