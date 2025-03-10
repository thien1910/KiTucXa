package com.project.KiTucXa.dto.request;

import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ContractDto {
   // @NotNull(message = "UserId is required")
     String userId;
   // @NotNull(message = "RoomId is required")
     String roomId;
   // @NotNull(message = "Start date is required")
     Date startDate;
  //  @NotNull(message = "End date is required")
     Date endDate;
  //  @NotNull(message = "Price is required")
     BigDecimal price;
   // @Enumerated(EnumType.STRING)
   // @NotNull(message = "Deposit status is required")
     DepositStatus depositStatus;
   // @Enumerated(EnumType.STRING)
   // @NotNull(message = "Contract status is required")
     ContractStatus contractStatus;
     String note;
}
