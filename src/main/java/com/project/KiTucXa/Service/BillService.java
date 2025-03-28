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

import static com.project.KiTucXa.Enum.ContractStatus.Active;
import static org.hibernate.engine.transaction.internal.jta.JtaStatusHelper.isActive;

@Service
@RequiredArgsConstructor
public class BillService {
    private final BillRepository billRepository;
    private final ContractRepository contractRepository;
    private final BillMapper billMapper;

    public BillResponse createBill(BillDto billDto) {
        Contract contract = contractRepository.findById(billDto.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Kiểm tra hợp đồng có đang active không
        if (!contract.getContractStatus().equals(Active)) {
            throw new AppException(ErrorCode.CONTRACT_INACTIVE);
        }

        Bill bill = billMapper.toBill(billDto);
        bill.setContract(contract);
        billRepository.save(bill);

        BillResponse response = billMapper.toBillResponse(bill);
        response.setFullName(contract.getUser().getFullName());

        // Nếu phương thức thanh toán là BANK_TRANSFER, tạo mã QR
        if (bill.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            String paymentInfo = "Chuyển khoản: " + bill.getSumPrice() + " VND";
            try {
                response.setQrCode(QRCodeGenerator.generateQRCode(paymentInfo));
            } catch (WriterException | IOException e) {
                throw new RuntimeException("Lỗi khi tạo mã QR", e);
            }
        }

        return response;
    }


    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(bill -> {
                    BillResponse response = billMapper.toBillResponse(bill);
                    response.setFullName(bill.getContract().getUser().getFullName()); // Gán fullName từ User
                    return response;
                })
                .collect(Collectors.toList());
    }


    public BillResponse getBillById(String billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_FOUND));

        BillResponse response = billMapper.toBillResponse(bill);

        // Gán fullName từ User trong Contract
        response.setFullName(bill.getContract().getUser().getFullName());

        return response;
    }

    public BillResponse updateBill(String billId, BillUpdateDto billDto) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        // Cập nhật thông tin Bill từ DTO
        billMapper.updateBill(bill, billDto);

        // Lưu lại bản ghi đã cập nhật
        bill = billRepository.save(bill);

        // Chuyển đổi sang DTO để trả về
        BillResponse response = billMapper.toBillResponse(bill);

        // Gán fullName từ User trong Contract
        response.setFullName(bill.getContract().getUser().getFullName());

        return response;
    }


    public void deleteBill(String billId) {
        if (!billRepository.existsById(billId)) {
            throw new AppException(ErrorCode.BILL_NOT_FOUND);
        }
        billRepository.deleteById(billId);
    }
    public List<BillResponse> getBillsByUserId(String userId) {
        List<Bill> bills = billRepository.findByUserId(userId);

        return bills.stream()
                .map(bill -> {
                    BillResponse response = billMapper.toBillResponse(bill);
                    response.setFullName(bill.getContract().getUser().getFullName()); // Gán tên user
                    return response;
                })
                .collect(Collectors.toList());
    }

}
