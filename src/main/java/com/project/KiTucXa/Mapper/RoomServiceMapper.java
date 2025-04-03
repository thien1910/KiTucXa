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
    @Mapping(target = "price", source = "roomServiceDto.price") // Bổ sung dòng này

    RoomService toRoomService(RoomServiceDto roomServiceDto, Room room, UtilityService utilityService);

    @Mapping(target = "roomId", source = "room.roomId")
    @Mapping(target = "utilityServiceId", source = "utilityService.utilityServiceId")

    RoomServiceResponse toRoomServiceResponse(RoomService roomService);
    void updateRoomService(@MappingTarget RoomService roomService, RoomServiceUpdateDto roomServiceDto);
}
//Test Plan này nhằm đảm bảo hệ thống Quản Lý Ký Túc Xá hoạt động ổn định, đáp ứng đầy đủ các yêu cầu nghiệp vụ và chất lượng theo SRS. Các nhóm phát triển, kiểm thử và quản lý dự án cần phối hợp chặt chẽ để cập nhật kế hoạch kiểm thử theo các thay đổi của dự án.