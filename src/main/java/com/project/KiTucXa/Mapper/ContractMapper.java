package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "roomId", target = "room.roomId")
    Contract toContract(ContractDto contractDto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "room.roomId", target = "roomId")
    ContractResponse toContractResponse(Contract contract);
    void updateContract(@MappingTarget Contract contract, ContractUpdateDto contractDto);
}
