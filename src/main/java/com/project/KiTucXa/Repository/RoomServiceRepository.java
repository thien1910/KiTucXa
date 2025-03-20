package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.RoomService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomService, String> {
}
