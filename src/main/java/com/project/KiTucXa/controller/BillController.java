package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.BillDto;
import com.project.KiTucXa.dto.response.BillResponse;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/add")
    ApiResponse<BillResponse> createBill (
            @RequestBody
            @Valid
            BillDto billDto
    ){
        ApiResponse<BillResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.createBill(billDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<BillResponse> getAllBills() {
        return billService.getAllBills();
    }

    @GetMapping("/{billId}")
    public BillResponse getBillById(
            @PathVariable("billId")
            String billId) {
        return billService.getBillById(billId);
    }

    @PutMapping("/update/{billId}")
    public BillResponse updateBill(
            @PathVariable("billId") String billId,
            @RequestBody BillUpdateDto billUpdateDto) {
        return billService.updateBill(billId, billUpdateDto);
    }

    @DeleteMapping("delete/{billId}")
    public String deleteBill(@PathVariable("billId") String billId) {
        billService.deleteBill(billId);
        return "Bill has been deleted";
    }
}
