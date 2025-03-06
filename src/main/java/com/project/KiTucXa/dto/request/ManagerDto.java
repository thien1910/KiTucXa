package com.project.KiTucXa.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ManagerDto {
     @NotNull(message = "UserId is required")
     String userId;
     @NotNull(message = "Department is required")
     String department;
}
