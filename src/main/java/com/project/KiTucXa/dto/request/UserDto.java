package com.project.KiTucXa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.KiTucXa.Enum.Gender;
import com.project.KiTucXa.Enum.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserDto {
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
    @NotNull(message = "roleId is required")
    private Long roleId;

}
