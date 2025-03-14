package com.project.KiTucXa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String managerId;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    String department;

}
