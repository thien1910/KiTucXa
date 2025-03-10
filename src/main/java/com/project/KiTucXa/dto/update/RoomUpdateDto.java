package com.project.KiTucXa.dto.update;

import com.project.KiTucXa.Enum.RoomStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomUpdateDto {
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
}
