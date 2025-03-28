package com.project.KiTucXa.Entity;

import jakarta.persistence.*;
import com.project.KiTucXa.Enum.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UtilityService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String utilityServiceId;
    String serviceName;
    String description;
    BigDecimal pricePerUnit;
    String calculationUnit;
    @Enumerated(EnumType.STRING)
    ContractStatus status;

}
