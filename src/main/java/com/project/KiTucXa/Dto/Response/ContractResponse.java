package com.project.KiTucXa.Dto.Response;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ContractResponse {
    String contractId;
    String userId;
    String roomId;
    Date startDate;
    Date endDate;
    BigDecimal price;
    @Enumerated(EnumType.STRING)
    DepositStatus depositStatus;

    @Enumerated(EnumType.STRING)
    ContractStatus contractStatus;

    String note;
    Timestamp createdAt;
    Timestamp updatedAt;
}
