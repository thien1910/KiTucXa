package com.project.KiTucXa.Dto.Response;

import com.project.KiTucXa.Enum.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;
    private Set<String> roles; // Thêm role
    private String fullName;
    String userId;
    Status status;


}
