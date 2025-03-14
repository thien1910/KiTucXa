package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.RoomServiceDto;
import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.response.RoomServiceResponse;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.update.RoomServiceUpdateDto;
import com.project.KiTucXa.dto.update.StudentUpdateDto;
import com.project.KiTucXa.entity.RoomService;
import com.project.KiTucXa.entity.Student;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.service.RoomServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-services")
public class RoomServiceController {
    @Autowired
    private RoomServiceService roomServiceService;

    @PostMapping("/add")
    ApiResponse<RoomServiceResponse> createRoomService(@RequestBody @Valid RoomServiceDto roomServiceDto) {
        ApiResponse<RoomServiceResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(roomServiceService.createRoomService(roomServiceDto));
        return apiResponse;
    }

    @GetMapping("/list")
    List<RoomServiceResponse> getAllRoomService() {
        return roomServiceService.getAllRoomServices();
    }

    @GetMapping("/{roomServiceId}")
    RoomServiceResponse getRoomServiceById(@PathVariable("roomServiceId") String roomServiceId) {
        return roomServiceService.getRoomServiceById(roomServiceId);
    }


    @DeleteMapping("/delete/{roomServiceId}")
    String deleteRoomService (@PathVariable String roomServiceId){
        roomServiceService.deleteRoomService(roomServiceId);
        return "RoomService has been Delete";
    }
}
