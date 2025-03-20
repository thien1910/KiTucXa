package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.RoomDto;
import com.project.KiTucXa.Dto.Response.RoomResponse;
import com.project.KiTucXa.Dto.Update.RoomUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
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
