package com.project.KiTucXa.Dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ManagerResponse {
    String managerId;
    String userId;
    String department;
    UserResponse user;
}
