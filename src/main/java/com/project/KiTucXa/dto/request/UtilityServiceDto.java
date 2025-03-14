package com.project.KiTucXa.dto.request;

import com.project.KiTucXa.Enum.ContractStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UtilityServiceDto {
     String serviceName;
     String description;
     BigDecimal pricePerUnit;
     String calculationUnit;
     @Enumerated(EnumType.STRING)
     ContractStatus status;

}
