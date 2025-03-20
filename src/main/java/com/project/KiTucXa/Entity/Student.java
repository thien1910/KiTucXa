package com.project.KiTucXa.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String studentId;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    String maSinhVien;
    public String getUserId() {
        return user != null ? user.getUserId() : null;
    }

}
