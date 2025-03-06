package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.UtilityServiceDto;
import com.project.KiTucXa.dto.response.UtilityServiceResponse;
import com.project.KiTucXa.dto.update.UtilityServiceUpdateDto;
import com.project.KiTucXa.entity.UtilityService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UtilityServiceMapper {
    UtilityService toUtilityService(UtilityServiceDto utilityServiceDto);
    UtilityServiceResponse toUtilityServiceResponse(UtilityService utilityService);
    void updateUtilityService(@MappingTarget UtilityService utilityService, UtilityServiceUpdateDto utilityServiceDto);
}
