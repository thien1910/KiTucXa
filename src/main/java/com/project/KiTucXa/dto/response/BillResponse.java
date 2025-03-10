package com.project.KiTucXa.dto.response;

import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class BillResponse {
    String billId;
    String contractId;
    BigDecimal sumPrice;
    java.sql.Date paymentDate;
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    BillStatus billStatus;
    String note;
    Timestamp createdAt;
    Timestamp updatedAt;
}
