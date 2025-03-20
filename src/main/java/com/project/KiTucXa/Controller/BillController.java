package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@Slf4j
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/add")
    ApiResponse<BillResponse> createBill(
            @RequestBody @Valid BillDto billDto) {
        ApiResponse<BillResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.createBill(billDto));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<BillResponse> getAllBills() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return billService.getAllBills();
    }

    @GetMapping("/{billId}")
    public BillResponse getBillById(
            @PathVariable("billId") String billId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return billService.getBillById(billId);
    }

    @PutMapping("/update/{billId}")
    public BillResponse updateBill(
            @PathVariable("billId") String billId,
            @RequestBody BillUpdateDto billUpdateDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return billService.updateBill(billId, billUpdateDto);
    }

    @DeleteMapping("delete/{billId}")
    public String deleteBill(@PathVariable("billId") String billId) {
        billService.deleteBill(billId);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // thêm SCOPE và userName đăng nhập (Vd: SCOPE_MANAGER)
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return "Bill has been deleted";
    }
}
