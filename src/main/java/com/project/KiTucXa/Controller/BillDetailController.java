package com.project.KiTucXa.Controller;

import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.BillDetailDto;
import com.project.KiTucXa.Dto.Response.BillDetailResponse;
import com.project.KiTucXa.Dto.Update.BillDetailUpdateDto;
import com.project.KiTucXa.Service.BillDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bill-details")
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

    @PutMapping("/update/{billDetailId}")
    public BillDetailResponse updateBillDetail(
            @PathVariable("billDetailId") String billDetailId,
            @RequestBody BillDetailUpdateDto billDetailUpdateDto) {
        return billDetailService.updateBillDetail(billDetailId, billDetailUpdateDto);
    }

    @DeleteMapping("/delete/{billDetailId}")
    public String deleteBillDetail(@PathVariable("billDetailId") String billDetailId) {
        billDetailService.deleteBillDetail(billDetailId);
        return "Bill Detail has been deleted";
    }
}
