package com.project.KiTucXa.Service;

import com.project.KiTucXa.Dto.Request.RoomDto;
import com.project.KiTucXa.Dto.Response.RoomResponse;
import com.project.KiTucXa.Dto.Update.RoomUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.RoomMapper;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private UserRepository userRepository;
    public List<RoomResponse> getAllRoom() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toRoomResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoom(String roomId) {
        return roomRepository.findById(roomId)
                .map(roomMapper::toRoomResponse)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public RoomResponse createRoom(RoomDto roomDto) {
        if (roomRepository.findByRoomName(roomDto.getRoomName()).isPresent()) {
            throw new AppException(ErrorCode.ROOM_ALREADY_EXISTS);
        }

        User user = userRepository.findById(roomDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Room room = roomMapper.toRoom(roomDto, user);
        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }


    public RoomResponse updateRoom(String roomId, RoomUpdateDto roomDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        roomMapper.updateRoom(room, roomDto);
        room = roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    public void deleteRoom(String roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Room not found");
        }
        roomRepository.deleteById(roomId);
    }
}
