package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.UtilityServiceDto;
import com.project.KiTucXa.Dto.Response.UtilityServiceResponse;
import com.project.KiTucXa.Dto.Update.UtilityServiceUpdateDto;
import com.project.KiTucXa.Service.UtilityServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/utility-services")
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

    @PutMapping("/update/{utilityServiceId}")
    public UtilityServiceResponse updateUtilityService(
            @PathVariable("roomId") String utilityServiceId,
            @RequestBody UtilityServiceUpdateDto utilityServiceDto) {
        return utilityServiceService.updateUtilityService(utilityServiceId, utilityServiceDto);
    }

    @DeleteMapping("/delete/{utilityServiceId}")
    public String deleteUtilityService(@PathVariable("utilityServiceId") String utilityServiceId) {
        utilityServiceService.deleteUtilityService(utilityServiceId);
        return "Room has been deleted";
    }
}
