package com.project.KiTucXa.entity;

import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
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
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;
    String userName;
    String passWord;
    String fullName;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String cccd;
    String phoneNumber;
    @Enumerated(EnumType.STRING)
    Status status;
    String country;

}
