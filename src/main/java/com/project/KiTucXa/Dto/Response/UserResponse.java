package com.project.KiTucXa.Dto.Response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String maSV;
    String userName;
    String passWord;
    String fullName;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String roomNameStudent;
    String cccd;
    String phoneNumber;
    @Enumerated(EnumType.STRING)
    Status status;
    String country;
    Set<String> roles;
    Timestamp createdAt;
    Timestamp updatedAt;
}