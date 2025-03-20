package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @PostMapping("/add")
    ApiResponse<ContractResponse> createContract(@RequestBody @Valid ContractDto contractDto) {
        ApiResponse<ContractResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(contractService.createContract(contractDto));
        return apiResponse;
    }

    @GetMapping("/list")
    public List<ContractResponse> getAllContracts() {
        return contractService.getAllContracts();
    }

    @GetMapping("/{contractId}")
    public ContractResponse getContractById(
            @PathVariable("contractId") String contractId) {
        return contractService.getContractById(contractId);
    }

    @PutMapping("/update/{contractId}")
    public ContractResponse updateContract(
            @PathVariable("contractId") String contractId,
            @RequestBody ContractUpdateDto contractUpdateDto) {
        return contractService.updateContract(contractId, contractUpdateDto);
    }

    @DeleteMapping("/delete/{contractId}")
    public String deleteContract(@PathVariable("contractId") String contractId) {
        contractService.deleteContract(contractId);
        return "Contract has been deleted";
    }
}
