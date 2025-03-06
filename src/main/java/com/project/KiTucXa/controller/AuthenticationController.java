package com.project.KiTucXa.controller;

import com.nimbusds.jose.JOSEException;
import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.AuthenticationRequest;
import com.project.KiTucXa.dto.request.IntrospectRequest;
import com.project.KiTucXa.dto.response.AuthenticationResponse;
import com.project.KiTucXa.dto.response.IntrospectResponse;
import com.project.KiTucXa.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest userDto){
        var result = authenticationService.authenticate(userDto);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest userDto)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(userDto);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
