package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, String> {
    boolean existsByDepartment(String department);

    Optional<Manager> findByUser_UserId(String userId);




}
