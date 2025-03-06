package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.RoomServiceDto;
import com.project.KiTucXa.dto.response.RoomServiceResponse;
import com.project.KiTucXa.dto.update.RoomServiceUpdateDto;
import com.project.KiTucXa.dto.update.RoomUpdateDto;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.entity.RoomService;
import com.project.KiTucXa.entity.UtilityService;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomServiceMapper {

    @Mapping(target = "room", source = "room")
    @Mapping(target = "utilityService", source = "utilityService")
    RoomService toRoomService(RoomServiceDto roomServiceDto, Room room, UtilityService utilityService);

    @Mapping(target = "roomId", source = "room.roomId")
    @Mapping(target = "utilityServiceId", source = "utilityService.utilityServiceId")
    RoomServiceResponse toRoomServiceResponse(RoomService roomService);
    void updateRoomService(@MappingTarget RoomService roomService, RoomServiceUpdateDto roomServiceDto);
}
