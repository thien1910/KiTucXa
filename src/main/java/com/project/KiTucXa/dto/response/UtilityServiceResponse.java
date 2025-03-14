package com.project.KiTucXa.dto.response;

import com.project.KiTucXa.Enum.ContractStatus;
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
public class UtilityServiceResponse {
    String utilityServiceId;
    String serviceName;
    String description;
    BigDecimal pricePerUnit;
    String calculationUnit;

    @Enumerated(EnumType.STRING)
    ContractStatus status;
    Timestamp createdAt;
    Timestamp updatedAt;
}
