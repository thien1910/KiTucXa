package com.project.KiTucXa.Dto.Update;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentUpdateDto {
    String note;
}
