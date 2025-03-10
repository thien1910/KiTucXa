package com.project.KiTucXa.dto.request;

import com.project.KiTucXa.Enum.RoomStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomDto {
    @NotNull(message = "UserId is required")
    String userId;
    @NotNull(message = "Room name is required")
    String roomName;
    @NotNull(message = "Department is required")
    String department;
    @NotNull(message = "Maximum occupancy is required")
    int maximumOccupancy;
    int currentOccupancy;
    @NotNull(message = "Room type is required")
    String roomType;
    @NotNull(message = "Room price is required")
    BigDecimal roomPrice;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Room status is required")
    RoomStatus roomStatus;
    String note;
}
