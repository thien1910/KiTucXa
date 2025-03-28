package com.project.KiTucXa.Dto.Update;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.project.KiTucXa.Enum.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomUpdateDto {
    String roomName;
    String department;
    int maximumOccupancy;
    int currentOccupancy;
    String roomType;
    BigDecimal roomPrice;
    @Enumerated(EnumType.STRING)
    RoomStatus roomStatus;
    String note;

    public RoomUpdateDto(String number, String a102, String it, int i, int i1, String deluxe, BigDecimal bigDecimal, Object o, String updatedNote) {
    }
}
