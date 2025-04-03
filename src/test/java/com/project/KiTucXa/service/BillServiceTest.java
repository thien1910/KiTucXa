package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.zxing.WriterException;
import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Entity.*;
import com.project.KiTucXa.Enum.BillStatus;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.PaymentMethod;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.BillMapper;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.ContractRepository;
import com.project.KiTucXa.Repository.RoomServiceRepository;
import com.project.KiTucXa.Service.BillService;
import com.project.KiTucXa.Service.QRCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project.KiTucXa.Entity.RoomService;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private BillMapper billMapper;

    @Mock
    private RoomServiceRepository roomServiceRepository;

    @InjectMocks
    private BillService billService;

    private Bill bill;
    private BillDto billDto;
    private Contract contract;
    private User user;
    private Room room;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Nguyen Van A");
        room = new Room();
        room.setRoomId("room123");
        room.setRoomPrice(BigDecimal.valueOf(5000000));

        contract = new Contract();
        contract.setContractId("contract123");
        contract.setUser(user);
        contract.setContractStatus(ContractStatus.Active);

        billDto = new BillDto();
        billDto.setContractId("contract123");
        billDto.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        bill = new Bill();
        bill.setContract(contract);
        bill.setSumPrice(new BigDecimal("100000"));
        bill.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        bill.setBillStatus(BillStatus.UNPAID);
        bill.setNote("Test Note");
//        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
//        when(roomServiceRepository.findByRoom_RoomId("room123"))
//                .thenReturn(List.of(new RoomService(new UtilityService("Water", new BigDecimal("1000000")))));
//        when(billMapper.toBill(any(BillDto.class))).thenReturn(bill);
//        when(billRepository.save(any(Bill.class))).thenReturn(bill);
//        when(billMapper.toBillResponse(any(Bill.class))).thenReturn(new BillResponse());
    }

    @Test
    void createBill_Success() {
        // Arrange:
        // Đảm bảo hợp đồng được trả về từ contractRepository
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        // Gán thông tin phòng vào hợp đồng (nếu logic yêu cầu)
        contract.setRoom(room);
        // Giả lập danh sách dịch vụ phòng (ví dụ: dịch vụ nước với giá 1000000)
        UtilityService waterService = new UtilityService("Water", new BigDecimal("1000000"));
        RoomService roomService = new RoomService();
        roomService.setUtilityService(waterService);

        when(roomServiceRepository.findByRoom_RoomId("room123")).thenReturn(List.of(roomService));
        // Giả lập mapper chuyển đổi từ DTO sang entity Bill
        when(billMapper.toBill(billDto)).thenReturn(bill);
        // Giả lập lưu bill và trả về chính đối tượng bill đó
        when(billRepository.save(any(Bill.class))).thenReturn(bill);
        // Giả lập mapper chuyển đổi Bill sang BillResponse
        BillResponse expectedResponse = new BillResponse();
        when(billMapper.toBillResponse(bill)).thenReturn(expectedResponse);

        // Act:
        BillResponse response = billService.createBill(billDto);

        // Assert:
        assertNotNull(response);
        // Giả sử rằng tổng tiền hóa đơn = giá phòng (5000000) + tổng giá dịch vụ (1000000) = 6000000
        assertEquals(new BigDecimal("6000000"), bill.getSumPrice());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void testCreateBill_ContractInactive() {
        // Sửa trạng thái contract thành không Active
        contract.setContractStatus(ContractStatus.Inactive);
        // Thiết lập contract trong repository
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));

        AppException exception = assertThrows(AppException.class, () -> billService.createBill(billDto));
        assertEquals(ErrorCode.CONTRACT_INACTIVE, exception.getErrorCode());
    }
    @Test
    void testGetAllBills_Empty() {
        // Giả lập repository trả về danh sách rỗng
        when(billRepository.findAll()).thenReturn(Collections.emptyList());

        // Gọi hàm getAllBills()
        List<BillResponse> responses = billService.getAllBills();

        // Kiểm tra kết quả trả về là danh sách rỗng
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(billRepository, times(1)).findAll();
    }
    @Test
    void testGetAllBills_Success() {
        // Arrange:
        // Giả lập một Bill với contract đã có user
        Bill bill1 = new Bill();
        bill1.setBillId("bill1");
        bill1.setContract(contract);  // contract đã có user (fullName "Nguyen Van A")

        // Giả lập repository trả về danh sách chứa 1 bill
        when(billRepository.findAll()).thenReturn(List.of(bill1));

        // Giả lập mapper chuyển đổi Bill -> BillResponse, và gán fullName từ contract.getUser()
        BillResponse billResponse = new BillResponse();
        billResponse.setFullName(contract.getUser().getFullName());
        when(billMapper.toBillResponse(any(Bill.class))).thenReturn(billResponse);

        // Act:
        List<BillResponse> responses = billService.getAllBills();

        // Assert:
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        // Kiểm tra fullName được gán chính xác từ user trong contract
        assertEquals("Nguyen Van A", responses.get(0).getFullName());
        verify(billRepository, times(1)).findAll();
    }

    @Test
    void testCreateBill_QRCodeGenerationException() {
        // Thiết lập các stub cần thiết để tạo bill thành công
        contract.setRoom(room);
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        when(roomServiceRepository.findByRoom_RoomId(room.getRoomId())).thenReturn(Collections.emptyList());
        when(billMapper.toBill(billDto)).thenReturn(bill);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        // Giả lập QRCodeGenerator.generateQRCode(...) ném WriterException
        try (var qrMock = mockStatic(QRCodeGenerator.class)) {
            qrMock.when(() -> QRCodeGenerator.generateQRCode(anyString()))
                    .thenThrow(new WriterException("QR error"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> billService.createBill(billDto));
            assertTrue(ex.getMessage().contains("Lỗi khi tạo mã QR"));
        }
    }
    @Test
    void testCreateBill_ContractNotFound() {
        when(contractRepository.findById("contract123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> billService.createBill(billDto));
    }
    @Test
    void testGetBillsByUserId_UserNotFound() {
        // Giả lập repository trả về danh sách rỗng khi không có bill nào liên quan đến userId "nonexistentUser"
        when(billRepository.findByUserId("nonexistentUser")).thenReturn(List.of());

        // Gọi method getBillsByUserId với userId không tồn tại
        List<BillResponse> result = billService.getBillsByUserId("nonexistentUser");

        // Kiểm tra rằng danh sách trả về không null và rỗng
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testGetBillsByUserId_NoBills() {
        // Giả lập: User không có hóa đơn (repository trả về danh sách rỗng)
        when(billRepository.findByUserId("nonExistingUser")).thenReturn(Collections.emptyList());

        // Act
        List<BillResponse> responses = billService.getBillsByUserId("nonExistingUser");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }


    @Test
    void testGetAllBills() {
        when(billRepository.findAll()).thenReturn(Collections.singletonList(bill));
        when(billMapper.toBillResponse(any())).thenReturn(new BillResponse());

        List<BillResponse> responses = billService.getAllBills();

        assertFalse(responses.isEmpty());
        verify(billRepository, times(1)).findAll();
    }
    @Test
    void testCreateBill_RoomServiceNotFound() {
        // Arrange:
        // Đảm bảo hợp đồng có thông tin phòng đã được gán
        contract.setRoom(room);
        // Giả lập repository tìm thấy hợp đồng
        when(contractRepository.findById("contract123")).thenReturn(Optional.of(contract));
        // Giả lập rằng không tìm thấy dịch vụ nào cho phòng (trả về danh sách rỗng)
        when(roomServiceRepository.findByRoom_RoomId("room123")).thenReturn(Collections.emptyList());
        // Giả lập mapper chuyển đổi từ BillDto sang Bill
        when(billMapper.toBill(billDto)).thenReturn(bill);
        // Giả lập lưu bill và trả về chính bill đó
        when(billRepository.save(any(Bill.class))).thenReturn(bill);
        // Giả lập mapper chuyển đổi Bill sang BillResponse
        BillResponse expectedResponse = new BillResponse();
        when(billMapper.toBillResponse(bill)).thenReturn(expectedResponse);

        // Act:
        BillResponse response = billService.createBill(billDto);

        // Assert:
        // Vì không có dịch vụ nào, totalServicePrice = 0 nên tổng tiền hóa đơn phải = room.getRoomPrice()
        assertEquals(room.getRoomPrice(), bill.getSumPrice());
        // Kiểm tra response không null (các assert khác sẽ do mapper xử lý)
        assertNotNull(response);
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testGetBillById_Success() {
        when(billRepository.findById("bill123")).thenReturn(Optional.of(bill));
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        BillResponse response = billService.getBillById("bill123");

        assertNotNull(response);
        verify(billRepository, times(1)).findById("bill123");
    }

    @Test
    void testGetBillById_NotFound() {
        when(billRepository.findById("bill123")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> billService.getBillById("bill123"));
    }

    @Test
    void testUpdateBill_Success() {
        BillUpdateDto billUpdateDto = new BillUpdateDto(new BigDecimal("200000"), new Date(System.currentTimeMillis()), PaymentMethod.CASH, BillStatus.PAID, "Updated Note");
        when(billRepository.findById("bill123")).thenReturn(Optional.of(bill));
        doNothing().when(billMapper).updateBill(bill, billUpdateDto);
        when(billRepository.save(bill)).thenReturn(bill);
        when(billMapper.toBillResponse(bill)).thenReturn(new BillResponse());

        BillResponse response = billService.updateBill("bill123", billUpdateDto);

        assertNotNull(response);
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testDeleteBill_Success() {
        when(billRepository.existsById("bill123")).thenReturn(true);
        doNothing().when(billRepository).deleteById("bill123");

        assertDoesNotThrow(() -> billService.deleteBill("bill123"));
        verify(billRepository, times(1)).deleteById("bill123");
    }

    @Test
    void testDeleteBill_NotFound() {
        when(billRepository.existsById("bill123")).thenReturn(false);

        assertThrows(AppException.class, () -> billService.deleteBill("bill123"));
    }
    @Test
    void testGetBillsByUserId_WithBills() {
        // Giả lập bill có hợp đồng có user
        Bill bill1 = new Bill();
        bill1.setBillId("bill1");
        bill1.setContract(contract);  // contract đã có user (fullName "Nguyen Van A")

        List<Bill> bills = List.of(bill1);
        when(billRepository.findByUserId("user123")).thenReturn(bills);

        BillResponse response = new BillResponse();
        when(billMapper.toBillResponse(any(Bill.class))).thenReturn(response);

        List<BillResponse> result = billService.getBillsByUserId("user123");

        // Kiểm tra kết quả: danh sách không rỗng và fullName được gán
        assertFalse(result.isEmpty());
        assertEquals("Nguyen Van A", result.get(0).getFullName());
    }
    @Test
    void testUpdateBill_NotFound() {
        BillUpdateDto billUpdateDto = new BillUpdateDto(new BigDecimal("200000"), new Date(System.currentTimeMillis()),
                PaymentMethod.CASH, BillStatus.PAID, "Updated Note");
        when(billRepository.findById("billNotFound")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                billService.updateBill("billNotFound", billUpdateDto)
        );
        assertEquals("Bill not found", exception.getMessage());
    }

}
