package com.project.KiTucXa.entity;

import jakarta.persistence.*;
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

public class BillDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String billDetailId;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    Bill bill;

    @ManyToOne
    @JoinColumn(name = "utility_service_id")
    UtilityService utilityService;

    Integer quantity;
    BigDecimal totalPrice;

}
