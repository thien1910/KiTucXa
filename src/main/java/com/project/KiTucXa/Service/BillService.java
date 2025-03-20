package com.project.KiTucXa.Service;


import com.google.zxing.WriterException;
import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Entity.Bill;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Enum.PaymentMethod;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.BillMapper;
import com.project.KiTucXa.Repository.BillRepository;
import com.project.KiTucXa.Repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        BillResponse response = billMapper.toBillResponse(bill);

        // Nếu phương thức thanh toán là BANK_TRANSFER, tạo mã QR
        if (bill.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            String paymentInfo = "Chuyển khoản: " + bill.getSumPrice() + " VND";
            try {
                response.setQrCode
                        (com.project.KiTucXa.Service.QRCodeGenerator.generateQRCode(paymentInfo));
            } catch (WriterException | IOException e) {
                throw new RuntimeException("Lỗi khi tạo mã QR", e);
            }
        }
        return response;
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
