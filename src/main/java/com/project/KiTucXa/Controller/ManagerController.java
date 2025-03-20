package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.ManagerDto;
import com.project.KiTucXa.Dto.Response.ManagerResponse;
import com.project.KiTucXa.Dto.Update.ManagerUpdateDto;
import com.project.KiTucXa.Service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/managers")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @PostMapping("/add")
    ApiResponse<ManagerResponse> createManager(@RequestBody @Valid ManagerDto managerDto) {
        ApiResponse<ManagerResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(managerService.createManager(managerDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<ManagerResponse> getAllManagers() {
        return managerService.getAllManagers();
    }

    @GetMapping("/{managerId}")
    public ManagerResponse getManager(
            @PathVariable("managerId") String managerId) {
        return managerService.getManager(managerId);
    }

    @PutMapping("update/{managerId}")
    public ManagerResponse updateManager(
            @PathVariable("managerId") String managerId,
            @RequestBody ManagerUpdateDto managerUpdateDto) {
        return managerService.updateManager(managerId, managerUpdateDto);
    }

    @DeleteMapping("delete/{managerId}")
    public String deleteManager(@PathVariable("managerId") String managerId) {
        managerService.deleteManager(managerId);
        return "Manager has been deleted";
    }
}
