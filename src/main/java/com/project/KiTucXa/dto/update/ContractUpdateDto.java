package com.project.KiTucXa.dto.update;

import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ContractUpdateDto {
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

}
