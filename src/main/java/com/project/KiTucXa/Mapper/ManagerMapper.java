package com.project.KiTucXa.Mapper;

import com.project.KiTucXa.Dto.Request.ManagerDto;
import com.project.KiTucXa.Dto.Response.ManagerResponse;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.ManagerUpdateDto;
import com.project.KiTucXa.Entity.Manager;
import com.project.KiTucXa.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(target = "user", source = "user")
    Manager toManager(ManagerDto managerDto, User user);

    @Mapping(target = "userId", source = "user.userId")
    ManagerResponse toManagerResponse(Manager manager, User user);

    void updateManager(@MappingTarget Manager manager, ManagerUpdateDto managerDto);

    @Named("toUserResponse")
    default UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .passWord(user.getPassWord())
                .fullName(user.getFullName())
                .gender(user.getGender())
//                .roomNameStudent(user.getRoomNameStudent())
                .cccd(user.getCccd())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
