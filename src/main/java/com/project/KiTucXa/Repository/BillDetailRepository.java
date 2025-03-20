package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDetailRepository extends JpaRepository<BillDetail, String> {
//    Optional<BillDetail> findById(int i);
}
