package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Mapper.ContractMapper;
import com.project.KiTucXa.Repository.ContractRepository;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractService contractService;

    private Contract contract;
    private ContractDto contractDto;
    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user123");

        room = new Room();
        room.setRoomId("room123");

        contractDto = new ContractDto("user123", "room123", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 86400000), new BigDecimal("5000000"), DepositStatus.COMPLETED, ContractStatus.Active, "Test Contract");

        contract = new Contract();
        contract.setUser(user);
        contract.setRoom(room);
        contract.setStartDate(new Date(System.currentTimeMillis()));
        contract.setEndDate(new Date(System.currentTimeMillis() + 86400000));
        contract.setPrice(new BigDecimal("5000000"));
        contract.setDepositStatus(DepositStatus.COMPLETED);
        contract.setContractStatus(ContractStatus.Active);
    }

    @Test
    void testCreateContract_Success() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(roomRepository.findById("room123")).thenReturn(Optional.of(room));
        when(contractMapper.toContract(contractDto)).thenReturn(contract);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toContractResponse(contract)).thenReturn(new ContractResponse());

        ContractResponse response = contractService.createContract(contractDto);

        assertNotNull(response);
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    void testCreateContract_UserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> contractService.createContract(contractDto));
    }

    @Test
    void testCreateContract_RoomNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(roomRepository.findById("room123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> contractService.createContract(contractDto));
    }

    @Test
    void testGetAllContracts() {
        when(contractRepository.findAll()).thenReturn(Collections.singletonList(contract));
        when(contractMapper.toContractResponse(any())).thenReturn(new ContractResponse());

        List<ContractResponse> responses = contractService.getAllContracts();

        assertFalse(responses.isEmpty());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void testGetContractById_Success() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        when(contractMapper.toContractResponse(contract)).thenReturn(new ContractResponse());

        ContractResponse response = contractService.getContractById("contract123");

        assertNotNull(response);
        verify(contractRepository, times(1)).findById("contract123");
    }

    @Test
    void testGetContractById_NotFound() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> contractService.getContractById("contract123"));
    }

    @Test
    void testUpdateContract_Success() {
        ContractUpdateDto contractUpdateDto = new ContractUpdateDto(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 172800000), new BigDecimal("6000000"), DepositStatus.UNPAID, ContractStatus.Inactive, "Updated Note");
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        doNothing().when(contractMapper).updateContract(contract, contractUpdateDto);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toContractResponse(contract)).thenReturn(new ContractResponse());

        ContractResponse response = contractService.updateContract("contract123", contractUpdateDto);

        assertNotNull(response);
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    void testDeleteContract_Success() {
        when(contractRepository.existsById("contract123")).thenReturn(true);
        doNothing().when(contractRepository).deleteById("contract123");

        assertDoesNotThrow(() -> contractService.deleteContract("contract123"));
        verify(contractRepository, times(1)).deleteById("contract123");
    }

    @Test
    void testDeleteContract_NotFound() {
        when(contractRepository.existsById("contract123")).thenReturn(false);

        assertThrows(AppException.class, () -> contractService.deleteContract("contract123"));
    }
}
