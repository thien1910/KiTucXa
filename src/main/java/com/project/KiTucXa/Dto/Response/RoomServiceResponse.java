package com.project.KiTucXa.Dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomServiceResponse {
    String roomServiceId;
    String roomId;
    Integer utilityServiceId;
    BigDecimal price;
    Timestamp createdAt;
    Timestamp updatedAt;
}
