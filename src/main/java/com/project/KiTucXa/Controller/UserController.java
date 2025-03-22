package com.project.KiTucXa.Controller;


import com.project.KiTucXa.Dto.Response.StudentResponse;
import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.UserCreationRequest;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.UserUpdateRequest;
import com.project.KiTucXa.Service.AuthenticationService;
import com.project.KiTucXa.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/add")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/list")
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }


    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }
    @GetMapping("/my-info")
    public UserResponse getMyInfo() {
        return userService.getMyInfo();
    }
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
   /* @PostMapping("/guest-login")
    public ApiResponse<AuthenticationResponse> guestLogin() {
        String guestToken = authenticationService.generateGuestToken();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<AuthenticationResponse>builder()
                .result(new AuthenticationResponse(guestToken, true))
                .build();
    }*/
}