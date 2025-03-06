package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
