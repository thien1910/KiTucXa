package com.project.KiTucXa.controller;

import com.project.KiTucXa.Comperments.JwtTokenUtil;
import com.project.KiTucXa.dto.request.UserLoginDTO;
import com.project.KiTucXa.dto.response.AuthenticationResponse;
import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.entity.Role;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/add")
    ApiResponse<User> createUser(@RequestBody @Valid UserDto userDto) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(userDto));
        return apiResponse;
    }

    @GetMapping("/list")
    ApiResponse<List<UserResponse>>getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("userName: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }


    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }



    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId,
            @RequestBody UserUpdateDto userDto) {
        return userService.updateUser(userId, userDto);
    }


    @DeleteMapping("/delete/{userId}")
    String deleteUser (@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been detele";
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            String token = userService.login(userLoginDTO.getUserName(), userLoginDTO.getPassWord());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/guest-login")
    public ApiResponse<AuthenticationResponse> guestLogin() {
        String guestToken = jwtTokenUtil.generateGuestToken();
        return ApiResponse.<AuthenticationResponse>builder()
                .result(new AuthenticationResponse(guestToken, true))
                .build();
    }

}