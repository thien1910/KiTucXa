package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.ContractDto;
import com.project.KiTucXa.dto.response.ContractResponse;
import com.project.KiTucXa.dto.update.ContractUpdateDto;
import com.project.KiTucXa.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @PostMapping("/add")
    ApiResponse<ContractResponse> createContract (@RequestBody @Valid ContractDto contractDto){
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
            @PathVariable("contractId")
            String contractId) {
        return contractService.getContractById(contractId);
    }

    @PutMapping("/{contractId}")
    public ContractResponse updateContract(
            @PathVariable("contractId") String contractId,
            @RequestBody ContractUpdateDto contractUpdateDto) {
        return contractService.updateContract(contractId, contractUpdateDto);
    }

    @DeleteMapping("/{contractId}")
    public String deleteContract(@PathVariable("contractId") String contractId) {
        contractService.deleteContract(contractId);
        return "Contract has been deleted";
    }
}
