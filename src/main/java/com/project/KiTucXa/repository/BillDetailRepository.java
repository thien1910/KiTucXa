package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillDetailRepository extends JpaRepository<BillDetail, String> {
//    Optional<BillDetail> findById(int i);
}
