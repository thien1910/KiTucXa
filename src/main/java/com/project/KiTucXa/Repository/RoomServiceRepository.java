package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.RoomService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomServiceRepository extends JpaRepository<RoomService, String> {
    List<RoomService> findByRoom_RoomId(String roomId);

}

