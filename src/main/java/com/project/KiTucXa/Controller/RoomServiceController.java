package com.project.KiTucXa.Controller;


import com.project.KiTucXa.Service.RoomServiceService;
import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.RoomServiceDto;
import com.project.KiTucXa.Dto.Response.RoomServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomServiceResponse>> getRoomServicesByRoomId(@PathVariable String roomId) {
        return ResponseEntity.ok(roomServiceService.getRoomServicesByRoomId(roomId));
    }

    @DeleteMapping("/delete/{roomServiceId}")
    String deleteRoomService (@PathVariable String roomServiceId){
        roomServiceService.deleteRoomService(roomServiceId);
        return "RoomService has been Delete";
    }
}
