package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByBill_BillId(String billId);
}
