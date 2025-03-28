package com.project.KiTucXa.Dto.Update;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentUpdateDto {
    String note;
    BigDecimal installmentCount; // số lần đóng (tối đa ba lần)

}
