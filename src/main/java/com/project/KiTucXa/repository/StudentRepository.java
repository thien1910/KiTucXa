package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    boolean existsByMaSinhVien(String maSinhVien);
    Optional<Student> findByMaSinhVien(String maSinhVien);
}
