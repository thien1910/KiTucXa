package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.RoomServiceDto;
import com.project.KiTucXa.dto.response.RoomServiceResponse;
import com.project.KiTucXa.dto.update.RoomServiceUpdateDto;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.entity.RoomService;
import com.project.KiTucXa.entity.UtilityService;
import com.project.KiTucXa.mapper.RoomServiceMapper;
import com.project.KiTucXa.repository.RoomRepository;
import com.project.KiTucXa.repository.RoomServiceRepository;
import com.project.KiTucXa.repository.UtilityServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceService {
    private final RoomServiceRepository roomServiceRepository;
    private final RoomRepository roomRepository;
    private final UtilityServiceRepository utilityServiceRepository;
    private final RoomServiceMapper roomServiceMapper;

    public RoomServiceResponse createRoomService(RoomServiceDto roomServiceDto) {
        Room room = roomRepository.findById(roomServiceDto.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        UtilityService utilityService = utilityServiceRepository.findById(roomServiceDto.getUtilityServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_SERVICE_NOT_FOUND));

        RoomService roomService = roomServiceMapper.toRoomService(roomServiceDto, room, utilityService);
        roomServiceRepository.save(roomService);

        return roomServiceMapper.toRoomServiceResponse(roomService);
    }

    public List<RoomServiceResponse> getAllRoomServices() {
        return roomServiceRepository.findAll().stream()
                .map(roomServiceMapper::toRoomServiceResponse)
                .collect(Collectors.toList());
    }

    public RoomServiceResponse getRoomServiceById(String roomServiceId) {
        RoomService roomService = roomServiceRepository.findById(roomServiceId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_SERVICE_NOT_FOUND));

        return roomServiceMapper.toRoomServiceResponse(roomService);
    }
    public RoomServiceResponse updateRoomService(String roomServiceId, RoomServiceUpdateDto roomServiceDto) {
        RoomService roomService = roomServiceRepository.findById(roomServiceId)
                .orElseThrow(() -> new RuntimeException("RoomService not found"));
        roomServiceMapper.updateRoomService(roomService, roomServiceDto);
        roomService = roomServiceRepository.save(roomService);
        return roomServiceMapper.toRoomServiceResponse(roomService);
    }

    public void deleteRoomService(String roomServiceId) {
        if (!roomServiceRepository.existsById(roomServiceId)) {
            throw new AppException(ErrorCode.ROOM_SERVICE_NOT_FOUND);
        }
        roomServiceRepository.deleteById(roomServiceId);
    }
}
