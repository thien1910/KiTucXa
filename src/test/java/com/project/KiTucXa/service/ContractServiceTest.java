package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.DepositStatus;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

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
    private User user;
    private Room room;
    private ContractDto contractDto;
    private ContractResponse contractResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user-123");

        room = new Room();
        room.setRoomId("room-456");

        contract = new Contract();
        contract.setContractId("contract-789");
        contract.setUser(user);
        contract.setRoom(room);
        contract.setStartDate(new Date());
        contract.setEndDate(new Date());
        contract.setPrice(new BigDecimal("5000000"));
        contract.setDepositStatus(DepositStatus.COMPLETED);
        contract.setContractStatus(ContractStatus.Active);
        contract.setNote("Hợp đồng ký 6 tháng");

        contractDto = new ContractDto("user-123", "room-456", new Date(), new Date(),
                new BigDecimal("5000000"), DepositStatus.COMPLETED, ContractStatus.Active, "Hợp đồng ký 6 tháng");

        contractResponse = new ContractResponse();

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(roomRepository.findById("room-456")).thenReturn(Optional.of(room));
        when(contractMapper.toContract(contractDto)).thenReturn(contract);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toContractResponse(contract)).thenReturn(contractResponse);
    }

    @Test
    void testCreateContract_Success() {
        ContractResponse result = contractService.createContract(contractDto);

        assertNotNull(result, "ContractResponse không được null");
        verify(contractRepository).save(contract);
        verify(contractMapper).toContractResponse(contract);
    }

    @Test
    void testCreateContract_UserNotFound() {
        when(userRepository.findById("user-123")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> contractService.createContract(contractDto));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(contractRepository, never()).save(any());
    }

    @Test
    void testCreateContract_RoomNotFound() {
        when(roomRepository.findById("room-456")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> contractService.createContract(contractDto));

        assertEquals(ErrorCode.ROOM_NOT_FOUND, exception.getErrorCode());
        verify(contractRepository, never()).save(any());
    }

    @Test
    void testGetAllContracts_Success() {
        when(contractRepository.findAll()).thenReturn(List.of(contract));
        when(contractMapper.toContractResponse(contract)).thenReturn(contractResponse);

        List<ContractResponse> result = contractService.getAllContracts();

        assertEquals(1, result.size());
        verify(contractRepository).findAll();
    }

    @Test
    void testGetContract_Success() {
        when(contractRepository.findById("contract-789")).thenReturn(Optional.of(contract));

        ContractResponse result = contractService.getContractById("contract-789");

        assertNotNull(result);
        verify(contractRepository).findById("contract-789");
    }

    @Test
    void testGetContract_NotFound() {
        when(contractRepository.findById("invalid-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> contractService.getContractById("invalid-id"));

        assertEquals("Contract not found", exception.getMessage());
    }

    @Test
    void testUpdateContract_Success() {
        ContractUpdateDto updateDto = new ContractUpdateDto("contract-789", "user-123", "room-456", new Date(), new Date(),
                new BigDecimal("5500000"), DepositStatus.UNPAID, ContractStatus.Inactive, "Gia hạn thêm 3 tháng");

        when(contractRepository.findById("contract-789")).thenReturn(Optional.of(contract));

        ContractResponse result = contractService.updateContract("contract-789", updateDto);

        assertNotNull(result);
        verify(contractRepository).save(contract);
    }

    @Test
    void testDeleteContract_Success() {
        when(contractRepository.existsById("contract-789")).thenReturn(true);

        assertDoesNotThrow(() -> contractService.deleteContract("contract-789"));

        verify(contractRepository).deleteById("contract-789");
    }

    @Test
    void testDeleteContract_NotFound() {
        when(contractRepository.existsById("invalid-id")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> contractService.deleteContract("invalid-id"));

        assertEquals("Contract not found", exception.getMessage());
    }
}
