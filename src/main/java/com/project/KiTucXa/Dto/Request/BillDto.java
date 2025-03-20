package com.project.KiTucXa.Dto.Request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillDto {
    @NotNull(message = "Contract is required")
    String contractId;
    BigDecimal sumPrice;
    java.sql.Date paymentDate;
     @Enumerated(EnumType.STRING)
     PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    BillStatus billStatus;
    String note;
}