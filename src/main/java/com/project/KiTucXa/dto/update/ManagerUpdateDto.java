package com.project.KiTucXa.dto.update;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ManagerUpdateDto {
    String managerId;
    String userId;
    String department;
}
