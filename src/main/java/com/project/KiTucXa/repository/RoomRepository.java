package com.project.KiTucXa.repository;

import com.project.KiTucXa.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    Optional<Room> findByRoomName(String roomName);
}
