package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
        List<Payment> findByBill_BillId(String billId);
}
