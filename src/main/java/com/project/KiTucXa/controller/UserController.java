package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    ApiResponse<User> createUser (@RequestBody @Valid UserDto userDto){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(userDto));
        return apiResponse;
    }
    @GetMapping("/list")
    List<User> getUser() {
        return userService.getUser();
    }


    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId){
        return userService.getUser(userId);
    }


    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId,
                    @RequestBody UserUpdateDto userDto){
        return userService.updateUser(userId, userDto);
    }


    @DeleteMapping("/{userId}")
    String deleteUser (@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been detele";
    }
}