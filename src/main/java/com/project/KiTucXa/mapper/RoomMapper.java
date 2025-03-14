package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.RoomDto;
import com.project.KiTucXa.dto.response.RoomResponse;
import com.project.KiTucXa.dto.update.RoomUpdateDto;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "user", source = "user")
    Room toRoom(RoomDto roomDto, User user);

    @Mapping(target = "userId", source = "user.userId")
    RoomResponse toRoomResponse(Room room);

    void updateRoom(@MappingTarget Room room, RoomUpdateDto roomDto);
}
