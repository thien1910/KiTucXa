package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByMaSinhVien(String maSinhVien);
    Optional<Student> findByMaSinhVien(String maSinhVien);

    Optional<Student> findByUser_UserId(String userId);
}
