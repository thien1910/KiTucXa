package com.project.KiTucXa.Dto.Request;

import jakarta.validation.constraints.NotNull;
import com.project.KiTucXa.Enum.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomDto {
    String userId;
    @NotNull(message = "Room name is required")
    String roomName;
    @NotNull(message = "Department is required")
    String department;
    @NotNull(message = "Maximum occupancy is required")
    int maximumOccupancy;
    @NotNull(message = "Room type is required")
    String roomType;
    @NotNull(message = "Room price is required")
    BigDecimal roomPrice;
    RoomStatus roomStatus;
    String note;
}
