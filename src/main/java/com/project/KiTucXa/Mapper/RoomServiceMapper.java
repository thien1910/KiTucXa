package com.project.KiTucXa.Mapper;

import com.project.KiTucXa.Dto.Request.RoomServiceDto;
import com.project.KiTucXa.Dto.Response.RoomServiceResponse;
import com.project.KiTucXa.Dto.Update.RoomServiceUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.RoomService;
import com.project.KiTucXa.Entity.UtilityService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
