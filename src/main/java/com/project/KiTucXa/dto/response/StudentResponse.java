package com.project.KiTucXa.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {
    String studentId;
    String userId;
    String maSinhVien;
    Timestamp createdAt;
    Timestamp updatedAt;
}
