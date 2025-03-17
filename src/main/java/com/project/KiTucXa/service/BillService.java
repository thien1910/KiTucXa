package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.BillDto;
import com.project.KiTucXa.dto.response.BillResponse;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.entity.Contract;
import com.project.KiTucXa.mapper.BillMapper;
import com.project.KiTucXa.repository.BillRepository;
import com.project.KiTucXa.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillService {
    private final BillRepository billRepository;
    private final ContractRepository contractRepository;
    private final BillMapper billMapper;

    public BillResponse createBill(BillDto billDto) {
        Contract contract = contractRepository.findById(billDto.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        Bill bill = billMapper.toBill(billDto);
        bill.setContract(contract);

        billRepository.save(bill);
        return billMapper.toBillResponse(bill);
    }

    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(billMapper::toBillResponse)
                .collect(Collectors.toList());
    }

    public BillResponse getBillById(String billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_FOUND));

        return billMapper.toBillResponse(bill);
    }
    public BillResponse updateBill(String billId, BillUpdateDto billDto) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(()-> new RuntimeException("Bill not found"));

        billMapper.updateBill(bill, billDto);

        return billMapper.toBillResponse(billRepository.save(bill));

    }

    public void deleteBill(String billId) {
        if (!billRepository.existsById(billId)) {
            throw new AppException(ErrorCode.BILL_NOT_FOUND);
        }
        billRepository.deleteById(billId);
    }
}
