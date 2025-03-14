package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateDto userDto);
}
