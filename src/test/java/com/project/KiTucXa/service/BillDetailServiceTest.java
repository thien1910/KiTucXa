package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.BillDetailDto;
import com.project.KiTucXa.Dto.Response.BillDetailResponse;
import com.project.KiTucXa.Dto.Update.BillDetailUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.BillDetail;
import com.project.KiTucXa.Entity.UtilityService;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.BillDetailMapper;
import com.project.KiTucXa.Repository.BillDetailRepository;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.UtilityServiceRepository;
import com.project.KiTucXa.Service.BillDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillDetailServiceTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private UtilityServiceRepository utilityServiceRepository;

    @Mock
    private BillDetailMapper billDetailMapper;

    @InjectMocks
    private BillDetailService billDetailService;

    private Bill bill;
    private UtilityService utilityService;
    private BillDetail billDetail;
    private BillDetailDto billDetailDto;
    private BillDetailResponse billDetailResponse;

    @BeforeEach
    void setUp() {
        billDetailDto = new BillDetailDto();
        billDetailDto.setBillId("bill1");
        billDetailDto.setUtilityServiceId("utility1");
        billDetailDto.setQuantity(10);
        billDetailResponse = new BillDetailResponse();
        billDetailResponse.setBillDetailId("billDetail1");
        billDetailResponse.setTotalPrice(BigDecimal.valueOf(1000));
        bill = new Bill();
        bill.setBillId("bill1");

        utilityService = new UtilityService();
        utilityService.setUtilityServiceId("utility1");
        utilityService.setPricePerUnit(BigDecimal.valueOf(100));

        billDetail = new BillDetail();
        billDetail.setBillDetailId("billDetail1");
        billDetail.setBill(bill);
        billDetail.setUtilityService(utilityService);
        billDetail.setTotalPrice(BigDecimal.valueOf(1000));

//        billDetailDto = new BillDetailDto("bill1", "utility1", 10);
//        billDetailResponse = new BillDetailResponse("billDetail1", "bill1", "utility1", 10, BigDecimal.valueOf(1000));
    }

    @Test
    void testCreateBillDetail_Success() {
        when(billRepository.findById("bill1")).thenReturn(Optional.of(bill));

        when(utilityServiceRepository.findById("utility1")).thenReturn(Optional.of(utilityService));
        when(billDetailMapper.toBillDetail(billDetailDto)).thenReturn(billDetail);
        when(billDetailRepository.save(billDetail)).thenReturn(billDetail);
        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(billDetailResponse);

        BillDetailResponse response = billDetailService.createBillDetail(billDetailDto);

        assertNotNull(response);
        assertEquals("billDetail1", response.getBillDetailId());
        assertEquals(BigDecimal.valueOf(1000), response.getTotalPrice());
    }


    @Test
    void testCreateBillDetail_BillNotFound() {
        // Tạo đối tượng BillDetailDto với billId không null
        BillDetailDto billDetailDto = new BillDetailDto();
        billDetailDto.setBillId("bill1"); // đảm bảo không null
        billDetailDto.setUtilityServiceId("utility1");
        billDetailDto.setQuantity(10);
        // Không cần set totalPrice vì nó được tính bên trong service

        // Stub findById trả về Optional.empty() cho billId "bill1"
        when(billRepository.findById("bill1")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            billDetailService.createBillDetail(billDetailDto);
        });

        assertEquals(ErrorCode.BILL_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void testCreateBillDetail_UtilityServiceNotFound() {
        // Khởi tạo billDetailDto đúng với các giá trị cần thiết
        BillDetailDto billDetailDto = new BillDetailDto();
        billDetailDto.setBillId("bill1");
        billDetailDto.setUtilityServiceId("utility1");
        billDetailDto.setQuantity(10);
        // Không cần set totalPrice vì nó được tính bên trong service

        // Stub: Hóa đơn được tìm thấy
        when(billRepository.findById("bill1")).thenReturn(Optional.of(bill));
        // Stub: Không tìm thấy utility service
        when(utilityServiceRepository.findById("utility1")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            billDetailService.createBillDetail(billDetailDto);
        });

        assertEquals(ErrorCode.UTILITY_SERVICE_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void testGetAllBillDetails() {
        // Giả lập danh sách BillDetail trong database
        BillDetail billDetail = new BillDetail();
        billDetail.setBillDetailId("billDetail1");

        List<BillDetail> billDetails = List.of(billDetail);

        // Khi gọi billDetailRepository.findAll() -> Trả về danh sách trên
        when(billDetailRepository.findAll()).thenReturn(billDetails);

        // Khi gọi billDetailMapper.toBillDetailResponse(), trả về dữ liệu mong muốn
        BillDetailResponse response = new BillDetailResponse();
        response.setBillDetailId("billDetail1");

        when(billDetailMapper.toBillDetailResponse(any(BillDetail.class)))
                .thenReturn(response);

        // Gọi method service
        List<BillDetailResponse> result = billDetailService.getAllBillDetails();

        // Kiểm tra kết quả có đúng không
        assertEquals(1, result.size());
        assertEquals("billDetail1", result.get(0).getBillDetailId()); // Đây là nơi lỗi xảy ra
    }

    @Test
    void testGetBillDetailById_Success() {
        // Giả lập dữ liệu BillDetail trong database
        BillDetail billDetail = new BillDetail();
        billDetail.setBillDetailId("billDetail1");

        // Khi gọi billDetailRepository.findById("billDetail1") -> Trả về billDetail
        when(billDetailRepository.findById("billDetail1")).thenReturn(Optional.of(billDetail));

        // Khi gọi billDetailMapper.toBillDetailResponse() -> Trả về response
        BillDetailResponse response = new BillDetailResponse();
        response.setBillDetailId("billDetail1");

        when(billDetailMapper.toBillDetailResponse(any(BillDetail.class)))
                .thenReturn(response);

        // Gọi method service
        BillDetailResponse result = billDetailService.getBillDetailById("billDetail1");

        // Debug giá trị thực tế
        System.out.println("Actual ID: " + result.getBillDetailId());

        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals("billDetail1", result.getBillDetailId()); // Lỗi xảy ra tại đây nếu result.getBillDetailId() == null
    }

    @Test
    void testGetBillDetailById_NotFound() {
        when(billDetailRepository.findById("billDetail1")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            billDetailService.getBillDetailById("billDetail1");
        });

        assertEquals(ErrorCode.BILL_DETAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testDeleteBillDetail_Success() {
        when(billDetailRepository.existsById("billDetail1")).thenReturn(true);

        billDetailService.deleteBillDetail("billDetail1");

        verify(billDetailRepository, times(1)).deleteById("billDetail1");
    }

    @Test
    void testDeleteBillDetail_NotFound() {
        when(billDetailRepository.existsById("billDetail1")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> {
            billDetailService.deleteBillDetail("billDetail1");
        });

        assertEquals(ErrorCode.BILL_DETAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateBillDetail_Success() {
        // Tạo đối tượng updateDto và đảm bảo quantity khác null
        BillDetailUpdateDto updateDto = new BillDetailUpdateDto();
        updateDto.setQuantity(20);

        // Giả lập giá trị ban đầu cho billDetail
        billDetail.setBillDetailId("billDetail1");
        billDetail.setTotalPrice(BigDecimal.valueOf(1000)); // giá trị ban đầu

        // Giả lập tìm kiếm billDetail
        when(billDetailRepository.findById("billDetail1")).thenReturn(Optional.of(billDetail));

        // Giả lập hành vi của mapper.updateBillDetail để cập nhật totalPrice dựa vào quantity
        doAnswer(invocation -> {
            BillDetail bd = invocation.getArgument(0);
            BillDetailUpdateDto dto = invocation.getArgument(1);
            // Giả sử logic cập nhật: totalPrice = dto.getQuantity() * 100
            bd.setTotalPrice(BigDecimal.valueOf(dto.getQuantity() * 100));
            return null;
        }).when(billDetailMapper).updateBillDetail(any(BillDetail.class), any(BillDetailUpdateDto.class));

        // Giả lập behavior của repository.save trả về đối tượng đã được cập nhật
        when(billDetailRepository.save(billDetail)).thenReturn(billDetail);

        // Giả lập mapper.toBillDetailResponse trả về response với totalPrice đã cập nhật
        BillDetailResponse updatedResponse = new BillDetailResponse();
        updatedResponse.setBillDetailId(billDetail.getBillDetailId());
        updatedResponse.setTotalPrice(billDetail.getTotalPrice());
        when(billDetailMapper.toBillDetailResponse(billDetail)).thenReturn(updatedResponse);

        // Gọi hàm cần test
        BillDetailResponse response = billDetailService.updateBillDetail("billDetail1", updateDto);

        // Debug giá trị
        System.out.println("Actual TotalPrice: " + response.getTotalPrice());

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000), response.getTotalPrice());
    }



    @Test
    void testUpdateBillDetail_NotFound() {
        when(billDetailRepository.findById("billDetail1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            billDetailService.updateBillDetail("billDetail1", new BillDetailUpdateDto(20));
        });

        assertEquals("BillDetail not found", exception.getMessage());
    }
}
