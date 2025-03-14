package com.project.KiTucXa.dto.response;

import com.project.KiTucXa.Enum.RoomStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String roomId;
    String userId;
    String roomName;
    String department;
    int maximumOccupancy;
    int currentOccupancy;
    String roomType;
    BigDecimal roomPrice;
    @Enumerated(EnumType.STRING)
    RoomStatus roomStatus;
    String note;
    Timestamp createdAt;
    Timestamp updatedAt;
}
