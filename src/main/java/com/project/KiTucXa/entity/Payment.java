package com.project.KiTucXa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String paymentId;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    Bill bill;
    private int installmentCount; // số lần đóng (tối đa ba lần)

    @Column(name = "note")
    String note;
}
