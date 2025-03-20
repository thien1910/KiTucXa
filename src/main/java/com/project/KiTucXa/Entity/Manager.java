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
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String managerId;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    User user;
    String department;
}
