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
public class Student extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String studentId;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    String maSinhVien;

}
