package com.project.KiTucXa.Dto.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // bỏ qua nhg message lỗi ko cần thiết phải hiển thị
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;
}