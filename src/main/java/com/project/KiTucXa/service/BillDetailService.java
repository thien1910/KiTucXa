package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.BillDetailDto;
import com.project.KiTucXa.dto.response.BillDetailResponse;
import com.project.KiTucXa.dto.response.BillResponse;
import com.project.KiTucXa.dto.update.BillDetailUpdateDto;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.entity.BillDetail;
import com.project.KiTucXa.entity.UtilityService;
import com.project.KiTucXa.mapper.BillDetailMapper;
import com.project.KiTucXa.repository.BillDetailRepository;
import com.project.KiTucXa.repository.BillRepository;
import com.project.KiTucXa.repository.UtilityServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillDetailService {
    private final BillDetailRepository billDetailRepository;
    private final BillRepository billRepository;
    private final UtilityServiceRepository utilityServiceRepository;
    private final BillDetailMapper billDetailMapper;

    public BillDetailResponse createBillDetail(BillDetailDto billDetailDto) {
        Bill bill = billRepository.findById(billDetailDto.getBillId())
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_FOUND));

        UtilityService utilityService = utilityServiceRepository.findById(billDetailDto.getUtilityServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_SERVICE_NOT_FOUND));

        BigDecimal totalPrice = utilityService.getPricePerUnit().multiply(BigDecimal.valueOf(billDetailDto.getQuantity()));

        BillDetail billDetail = billDetailMapper.toBillDetail(billDetailDto);
        billDetail.setBill(bill);
        billDetail.setUtilityService(utilityService);
        billDetail.setTotalPrice(totalPrice);

        billDetailRepository.save(billDetail);
        return billDetailMapper.toBillDetailResponse(billDetail);
    }

    public List<BillDetailResponse> getAllBillDetails() {
        return billDetailRepository.findAll().stream()
                .map(billDetailMapper::toBillDetailResponse)
                .collect(Collectors.toList());
    }

    public BillDetailResponse getBillDetailById(String billDetailId) {
        BillDetail billDetail = billDetailRepository.findById(billDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_DETAIL_NOT_FOUND));

        return billDetailMapper.toBillDetailResponse(billDetail);
    }

    public void deleteBillDetail(String billDetailId) {
        if (!billDetailRepository.existsById(billDetailId)) {
            throw new AppException(ErrorCode.BILL_DETAIL_NOT_FOUND);
        }
        billDetailRepository.deleteById(billDetailId);
    }
    public BillDetailResponse updateBillDetail(String billDetailId, BillDetailUpdateDto billDetailDto) {

        BillDetail billDetail = billDetailRepository.findById(billDetailId)
                .orElseThrow(()-> new RuntimeException("BillDetail not found"));

        billDetailMapper.updateBillDetail(billDetail, billDetailDto);

        return billDetailMapper.toBillDetailResponse(billDetailRepository.save(billDetail));

    }
}
