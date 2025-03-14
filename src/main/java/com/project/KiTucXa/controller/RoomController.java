package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.request.RoomDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.response.RoomResponse;
import com.project.KiTucXa.dto.update.RoomUpdateDto;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/add")
    ApiResponse<RoomResponse> createRoom(@RequestBody @Valid RoomDto roomDto) {
        ApiResponse<RoomResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(roomService.createRoom(roomDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRoom();
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(
            @PathVariable("roomId") String roomId) {
        return roomService.getRoom(roomId);
    }

    @PutMapping("/update/{roomId}")
    public RoomResponse updateRoom(
            @PathVariable("roomId") String roomId,
            @RequestBody RoomUpdateDto roomUpdateDto) {
        return roomService.updateRoom(roomId, roomUpdateDto);
    }

    @DeleteMapping("/delete/{roomId}")
    public String deleteRoom(@PathVariable("roomId") String roomId) {
        roomService.deleteRoom(roomId);
        return "Room has been deleted";
    }
}
