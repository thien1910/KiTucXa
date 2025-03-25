package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ContractRepository extends JpaRepository<Contract, String> {
    List<Contract> findByUser_UserId(String userId);

}
