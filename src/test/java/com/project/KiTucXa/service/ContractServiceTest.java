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
import com.project.KiTucXa.Enum.RoomStatus;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

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
        room.setMaximumOccupancy(1); // hoặc số phù hợp
        room.setCurrentOccupancy(0);

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
    void testUpdateContract_NotFound() {
        // Tạo dữ liệu đầu vào cho việc update
        ContractUpdateDto contractUpdateDto = new ContractUpdateDto(
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 172800000),
                new BigDecimal("6000000"),
                DepositStatus.UNPAID,
                ContractStatus.Inactive,
                "Updated Note"
        );

        // Giả lập repository trả về Optional.empty() khi tìm contract theo id
        when(contractRepository.findById("contract123")).thenReturn(Optional.empty());

        // Thực hiện kiểm tra ngoại lệ: mong đợi RuntimeException với thông báo "Contract not found"
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                contractService.updateContract("contract123", contractUpdateDto)
        );
        assertEquals("Contract not found", exception.getMessage());
    }




    @Test
    void testDeleteContract_Success() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));

        doNothing().when(contractRepository).deleteById("contract123");

        assertDoesNotThrow(() -> contractService.deleteContract("contract123"));
        verify(contractRepository, times(1)).deleteById("contract123");
    }

    @Test
    void testDeleteContract_NotFound() {
        when(contractRepository.existsById("contract123")).thenReturn(false);

        assertThrows(AppException.class, () -> contractService.deleteContract("contract123"));
    }
    @Test
    void testCreateContract_RoomFull() {
        // Arrange
        ContractDto dto = new ContractDto("user123", "room123",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 86400000),
                new BigDecimal("5000000"), DepositStatus.COMPLETED, ContractStatus.Active, "Test Contract");

        // Giả lập user tồn tại
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // Giả lập room có currentOccupancy bằng maximumOccupancy
        room.setCurrentOccupancy(room.getMaximumOccupancy());
        when(roomRepository.findById("room123")).thenReturn(Optional.of(room));

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> contractService.createContract(dto));
        // Giả sử ErrorCode.ROOM_FULL là mã lỗi khi phòng đầy
        assertEquals(ErrorCode.ROOM_FULL, ex.getErrorCode());
    }
    @Test
    void testCreateContract_UserHasActiveContract() {
        // Arrange
        ContractDto dto = new ContractDto("user123", "room123",
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 86400000),
                new BigDecimal("5000000"), DepositStatus.COMPLETED, ContractStatus.Active, "Test Contract");

        // Giả lập user tồn tại
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        // Giả lập room tồn tại và còn chỗ trống
        when(roomRepository.findById("room123")).thenReturn(Optional.of(room));

        // Giả lập rằng user đã có hợp đồng active chưa hết hạn
        Contract activeContract = new Contract();
        activeContract.setContractStatus(ContractStatus.Active);
        activeContract.setEndDate(new Date(System.currentTimeMillis() + 100000)); // chưa hết hạn
        when(contractRepository.findByUser_UserId("user123")).thenReturn(List.of(activeContract));

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> contractService.createContract(dto));
        assertEquals(ErrorCode.USER_HAS_ACTIVE_CONTRACT, ex.getErrorCode());
    }
    @Test
    void testDeleteContract_RoomOccupancyDecrease() {
        // Arrange: Tạo contract với room có currentOccupancy > 0 và trạng thái full_room
        room.setCurrentOccupancy(1);
        room.setMaximumOccupancy(1);
        room.setRoomStatus(RoomStatus.full_room);
        contract.setRoom(room);
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));

        // Giả lập delete hợp đồng
        doNothing().when(contractRepository).deleteById("contract123");
        when(roomRepository.save(room)).thenReturn(room);

        // Act
        assertDoesNotThrow(() -> contractService.deleteContract("contract123"));

        // Assert: currentOccupancy giảm đi 1 và trạng thái phòng được cập nhật thành empty_room (hoặc AVAILABLE)
        assertEquals(0, room.getCurrentOccupancy());
        assertEquals(RoomStatus.empty_room, room.getRoomStatus());
        verify(roomRepository, times(1)).save(room);
    }
    @Test
    void testGetContractsByUserId_WithContracts() {
        // Arrange: Tạo một contract mẫu
        Contract contract1 = new Contract();
        contract1.setContractId("c1");
        when(contractRepository.findByUser_UserId("user123")).thenReturn(List.of(contract1));

        // Act
        List<Contract> result = contractService.getContractsByUserId("user123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    void testGetContractsByUserId_NoContracts() {
        when(contractRepository.findByUser_UserId("user456")).thenReturn(Collections.emptyList());

        List<Contract> result = contractService.getContractsByUserId("user456");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

//    @Test
//    void testUpdateExpiredContracts() {
//        // Arrange: Tạo hợp đồng active đã hết hạn
//        Contract expiredContract = new Contract();
//        expiredContract.setContractId("expired1");
//        expiredContract.setContractStatus(ContractStatus.Active);
//        expiredContract.setEndDate(new Date(System.currentTimeMillis() - 100000)); // đã hết hạn
//
//        // Tạo room với currentOccupancy = 2
//        Room roomForExpired = new Room();
//        roomForExpired.setRoomId("room1");
//        roomForExpired.setCurrentOccupancy(2);
//        roomForExpired.setMaximumOccupancy(3);
//        roomForExpired.setRoomStatus(RoomStatus.full_room);
//        expiredContract.setRoom(roomForExpired);
//
//        // Stub: Sử dụng any(ContractStatus.class) để đảm bảo trả về expiredContract
//        when(contractRepository.findByEndDateBeforeAndContractStatus(any(Date.class), any(ContractStatus.class)))
//                .thenReturn(List.of(expiredContract));
//        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(contractRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act: Gọi phương thức scheduled updateExpiredContracts()
//        contractService.updateExpiredContracts();
//
//        // Capture danh sách hợp đồng được lưu qua contractRepository.saveAll
//        ArgumentCaptor<List<Contract>> contractCaptor = ArgumentCaptor.forClass(List.class);
//        verify(contractRepository).saveAll(contractCaptor.capture());
//        List<Contract> savedContracts = contractCaptor.getValue();
//
//        // Assert:
//        // Kiểm tra rằng danh sách các hợp đồng đã được cập nhật không rỗng
//        assertFalse(savedContracts.isEmpty(), "Không có hợp đồng nào được cập nhật");
//        // Lấy hợp đồng đã được cập nhật
//        Contract updatedContract = savedContracts.get(0);
//        // Kiểm tra trạng thái của hợp đồng đã được cập nhật thành Inactive
//        assertEquals(ContractStatus.Inactive, updatedContract.getContractStatus());
//        // Kiểm tra currentOccupancy của room đã giảm đi 1 (từ 2 xuống 1)
//        assertEquals(1, roomForExpired.getCurrentOccupancy());
//        verify(roomRepository, times(1)).save(roomForExpired);
//    }





    @Test
    void testGetAllContracts_UpdateExpiredContracts() {
        // Arrange: Tạo contract active đã hết hạn
        Contract contractExpired = new Contract();
        contractExpired.setContractId("expired2");
        contractExpired.setContractStatus(ContractStatus.Active);
        contractExpired.setEndDate(new Date(System.currentTimeMillis() - 100000)); // đã hết hạn

        // Stub repository trả về danh sách chứa contractExpired
        when(contractRepository.findAll()).thenReturn(List.of(contractExpired));
        // Giả lập save() khi cập nhật contract
        when(contractRepository.save(contractExpired)).thenReturn(contractExpired);
        ContractResponse resp = new ContractResponse();
        when(contractMapper.toContractResponse(contractExpired)).thenReturn(resp);

        // Act
        List<ContractResponse> responses = contractService.getAllContracts();

        // Assert: contractExpired nên có trạng thái Inactive
        assertEquals(ContractStatus.Inactive, contractExpired.getContractStatus());
        assertEquals(1, responses.size());
    }


}
