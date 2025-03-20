package com.project.KiTucXa.Repository;


import com.project.KiTucXa.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findByRoomName(String roomName);
}
