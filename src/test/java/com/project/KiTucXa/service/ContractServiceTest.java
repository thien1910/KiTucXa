package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.ContractDto;
import com.project.KiTucXa.dto.response.ContractResponse;
import com.project.KiTucXa.dto.update.ContractUpdateDto;
import com.project.KiTucXa.entity.Contract;
import com.project.KiTucXa.mapper.ContractMapper;
import com.project.KiTucXa.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractService contractService;

    private Contract contract;
    private ContractDto contractDto;
    private ContractUpdateDto contractUpdateDto;
    private ContractResponse contractResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contract = new Contract();
        // contract.setContractId("1");
        // contract.setStudent("stu123");
        // contract.("room456");
        contract.setPrice(BigDecimal.valueOf(1000));

        contractDto = new ContractDto();
        contractDto.setStudent_id("stu123");
        contractDto.setRoom_id("room456");
        contractDto.setPrice(BigDecimal.valueOf(1000));

        contractUpdateDto = new ContractUpdateDto();
        contractUpdateDto.setPrice(BigDecimal.valueOf(1200));

        contractResponse = new ContractResponse();
        contractResponse.setContractId("1");
        contractResponse.setStudentId("stu123");
        contractResponse.setRoomId("room456");
        contractResponse.setPrice(BigDecimal.valueOf(1000));
    }

    @Test
    void getAllContracts_ShouldReturnList() {
        when(contractRepository.findAll()).thenReturn(List.of(contract));
        when(contractMapper.toContractResponse(contract)).thenReturn(contractResponse);

        List<ContractResponse> contracts = contractService.getAllContract();
        assertEquals(1, contracts.size());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void getContractById_ShouldReturnContractResponse() {
        when(contractRepository.findById("1")).thenReturn(Optional.of(contract));
        when(contractMapper.toContractResponse(contract)).thenReturn(contractResponse);

        ContractResponse response = contractService.getContract("1");
        assertEquals("1", response.getContractId());
        verify(contractRepository, times(1)).findById("1");
    }

    @Test
    void getContractById_ShouldThrowException_WhenNotFound() {
        when(contractRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> contractService.getContract("999"));
    }

    @Test
    void createContract_ShouldReturnCreatedContract() {
        when(contractMapper.toContract(contractDto)).thenReturn(contract);
        when(contractRepository.save(contract)).thenReturn(contract);

        Contract created = contractService.createContract(contractDto);
        assertEquals(contract, created);
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    void updateContract_ShouldReturnUpdatedContract() {
        when(contractRepository.findById("1")).thenReturn(Optional.of(contract));
        doNothing().when(contractMapper).updateContract(contract, contractUpdateDto);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toContractResponse(contract)).thenReturn(contractResponse);

        ContractResponse updatedResponse = contractService.updateContract("1", contractUpdateDto);
        assertEquals("1", updatedResponse.getContractId());
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    void deleteContract_ShouldCallDelete() {
        when(contractRepository.existsById("1")).thenReturn(true);
        doNothing().when(contractRepository).deleteById("1");

        contractService.deleteContract("1");
        verify(contractRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteContract_ShouldThrowException_WhenNotFound() {
        when(contractRepository.existsById("999")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> contractService.deleteContract("999"));
    }
}
