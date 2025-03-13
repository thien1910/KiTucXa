package com.project.KiTucXa.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // khoi tao ko tham so
@AllArgsConstructor// day du tham so
public class UserLoginDTO {
    private String userName;
    private String passWord;
}
