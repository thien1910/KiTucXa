package com.project.KiTucXa.Dto.Request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserCreationRequest {
    String maSV;
    @Size(min = 3, message = "USERNAME_INVALID")
    String userName;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String passWord;
    String fullName;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String cccd;
    String phoneNumber;
    @Enumerated(EnumType.STRING)
    Status status;
    String country;

    public UserCreationRequest(String testuser, String password, String testUser, Gender gender, String number, String number1, Status status, String vietnam) {
    }
}
