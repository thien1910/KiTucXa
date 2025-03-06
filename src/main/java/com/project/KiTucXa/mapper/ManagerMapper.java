package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.update.ManagerUpdateDto;
import com.project.KiTucXa.entity.Manager;
import com.project.KiTucXa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
    @Mapping(target = "user", source = "user")
    Manager toManager(ManagerDto managerDto, User user);

    @Mapping(target = "userId", source = "user.userId")
    ManagerResponse toManagerResponse(Manager manager);

    void updateManager(@MappingTarget Manager manager, ManagerUpdateDto managerDto);
}
