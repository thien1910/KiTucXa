package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, String> {
    boolean existsByDepartment(String department);
}
