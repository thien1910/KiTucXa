package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("SELECT b FROM Bill b JOIN b.contract c WHERE c.user.id = :userId")
    List<Bill> findByUserId(@Param("userId") String userId);
}
