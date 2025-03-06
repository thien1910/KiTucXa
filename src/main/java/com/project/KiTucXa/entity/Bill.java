package com.project.KiTucXa.entity;

import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
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


public class Bill extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String billId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
     Contract contract;
     BigDecimal sumPrice;
     java.sql.Date paymentDate;
    @Enumerated(EnumType.STRING)
     PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
     BillStatus billStatus;
     String note;


}
