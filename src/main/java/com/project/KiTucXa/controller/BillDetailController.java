package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.BillDetailDto;
import com.project.KiTucXa.dto.response.BillDetailResponse;
import com.project.KiTucXa.dto.update.BillDetailUpdateDto;
import com.project.KiTucXa.entity.BillDetail;
import com.project.KiTucXa.service.BillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bill-details")
public class BillDetailController {
    @Autowired
    private BillDetailService billDetailService;

    @PostMapping("/add")
    ApiResponse<BillDetailResponse> createBillDetail(
            @RequestBody @Valid BillDetailDto billDetailDto) {
        ApiResponse<BillDetailResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billDetailService.createBillDetail(billDetailDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<BillDetailResponse> getAllBillDetails() {
        return billDetailService.getAllBillDetails();
    }

    @GetMapping("/{billDetailId}")
    public BillDetailResponse getBillDetailById(
            @PathVariable("billDetailId") String billDetailId) {
        return billDetailService.getBillDetailById(billDetailId);
    }

    @PutMapping("/{billDetailId}")
    public BillDetailResponse updateBillDetail(
            @PathVariable("billDetailId") String billDetailId,
            @RequestBody BillDetailUpdateDto billDetailUpdateDto) {
        return billDetailService.updateBillDetail(billDetailId, billDetailUpdateDto);
    }

    @DeleteMapping("/{billDetailId}")
    public String deleteBillDetail(@PathVariable("billDetailId") String billDetailId) {
        billDetailService.deleteBillDetail(billDetailId);
        return "Bill Detail has been deleted";
    }
}
