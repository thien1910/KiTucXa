package com.project.KiTucXa.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ManagerResponse {
    String managerId;
    String userId;
    String department;
    Timestamp createdAt;
    Timestamp updatedAt;
}
