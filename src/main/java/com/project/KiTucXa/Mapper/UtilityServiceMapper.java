package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.UtilityServiceDto;
import com.project.KiTucXa.Dto.Response.UtilityServiceResponse;
import com.project.KiTucXa.Dto.Update.UtilityServiceUpdateDto;
import com.project.KiTucXa.Entity.UtilityService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UtilityServiceMapper {
    UtilityService toUtilityService(UtilityServiceDto utilityServiceDto);
    UtilityServiceResponse toUtilityServiceResponse(UtilityService utilityService);
    void updateUtilityService(@MappingTarget UtilityService utilityService, UtilityServiceUpdateDto utilityServiceDto);
}
