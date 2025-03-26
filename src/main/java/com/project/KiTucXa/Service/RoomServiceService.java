package com.project.KiTucXa.Service;



import com.project.KiTucXa.Dto.Request.RoomServiceDto;
import com.project.KiTucXa.Dto.Response.RoomServiceResponse;
import com.project.KiTucXa.Dto.Update.RoomServiceUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.RoomService;
import com.project.KiTucXa.Entity.UtilityService;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.RoomServiceMapper;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.RoomServiceRepository;
import com.project.KiTucXa.Repository.UtilityServiceRepository;
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

        com.project.KiTucXa.Entity.RoomService roomService = roomServiceMapper.toRoomService
                (roomServiceDto, room, utilityService);
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
        com.project.KiTucXa.Entity.RoomService roomService = roomServiceRepository.findById(roomServiceId)
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
    public List<RoomServiceResponse> getRoomServicesByRoomId(String roomId) {
        List<RoomService> roomServices = roomServiceRepository.findByRoom_RoomId(roomId);

        return roomServices.stream()
                .map(roomServiceMapper::toRoomServiceResponse)
                .collect(Collectors.toList());
    }


}
