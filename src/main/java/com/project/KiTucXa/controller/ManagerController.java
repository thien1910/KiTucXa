package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.update.ManagerUpdateDto;
import com.project.KiTucXa.entity.Manager;
import com.project.KiTucXa.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
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
        return managerService.getAllManager();
    }

    @GetMapping("/{managerId}")
    public ManagerResponse getManager(
            @PathVariable("managerId") String managerId) {
        return managerService.getManager(managerId);
    }

    @PutMapping("/{managerId}")
    public ManagerResponse updateManager(
            @PathVariable("managerId") String managerId,
            @RequestBody ManagerUpdateDto managerUpdateDto) {
        return managerService.updateManager(managerId, managerUpdateDto);
    }

    @DeleteMapping("/{managerId}")
    public String deleteManager(@PathVariable("managerId") String managerId) {
        managerService.deleteManager(managerId);
        return "Manager has been deleted";
    }
}
