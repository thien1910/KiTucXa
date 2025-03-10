package com.project.KiTucXa.entity;

import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String contractId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;
    Date startDate;
    Date endDate;
    BigDecimal price;
    @Enumerated(EnumType.STRING)
    DepositStatus depositStatus;

    @Enumerated(EnumType.STRING)
    ContractStatus contractStatus;

    String note;

}
