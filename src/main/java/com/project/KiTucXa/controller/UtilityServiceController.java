package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.RoomDto;
import com.project.KiTucXa.dto.request.UtilityServiceDto;
import com.project.KiTucXa.dto.response.RoomResponse;
import com.project.KiTucXa.dto.response.UtilityServiceResponse;
import com.project.KiTucXa.dto.update.RoomUpdateDto;
import com.project.KiTucXa.dto.update.UtilityServiceUpdateDto;
import com.project.KiTucXa.entity.UtilityService;
import com.project.KiTucXa.service.UtilityServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/utility-services")
public class UtilityServiceController {
    @Autowired
    private UtilityServiceService utilityServiceService;

    @PostMapping("/add")
    ApiResponse<UtilityServiceResponse> createUtilityService(@RequestBody @Valid UtilityServiceDto utilityServiceDto) {
        ApiResponse<UtilityServiceResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(utilityServiceService.createUtilityService(utilityServiceDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<UtilityServiceResponse> getAllUtilityServices() {
        return utilityServiceService.getAllUtilityServices();
    }

    @GetMapping("/{utilityServiceId}")
    public UtilityServiceResponse getRoom(
            @PathVariable("utilityServiceId") String utilityServiceId) {
        return utilityServiceService.getUtilityServiceById(utilityServiceId);
    }

    @PutMapping("/{utilityServiceId}")
    public UtilityServiceResponse updateUtilityService(
            @PathVariable("roomId") String utilityServiceId,
            @RequestBody UtilityServiceUpdateDto utilityServiceDto) {
        return utilityServiceService.updateUtilityService(utilityServiceId, utilityServiceDto);
    }

    @DeleteMapping("/{utilityServiceId}")
    public String deleteUtilityService(@PathVariable("utilityServiceId") String utilityServiceId) {
        utilityServiceService.deleteUtilityService(utilityServiceId);
        return "Room has been deleted";
    }
}
