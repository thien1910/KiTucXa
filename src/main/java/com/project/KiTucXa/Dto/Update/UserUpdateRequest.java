package com.project.KiTucXa.Dto.Update;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserUpdateRequest {
     String maSV;
     String passWord;
     String fullName;
     @Enumerated(EnumType.STRING)
     Gender gender;
     String roomName;
     String cccd;
     String phoneNumber;
     @Enumerated(EnumType.STRING)
     Status status;
     String country;


}
