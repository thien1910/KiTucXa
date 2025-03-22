package com.project.KiTucXa.Dto.Response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
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
    String qrCode;
    Timestamp createdAt;
    Timestamp updatedAt;
    String fullName;
}
