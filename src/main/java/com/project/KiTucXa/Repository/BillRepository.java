package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, String> {
}
