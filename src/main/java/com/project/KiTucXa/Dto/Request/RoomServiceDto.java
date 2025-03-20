package com.project.KiTucXa.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RoomServiceDto {
     @NotNull(message = "RoomId is required")
     String roomId;
     @NotNull(message = "Utility Service ID is required")
     String utilityServiceId;
     @NotNull(message = "Price is required")
     BigDecimal price;
}
