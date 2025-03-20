package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.UserCreationRequest;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.UserUpdateRequest;
import com.project.KiTucXa.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}