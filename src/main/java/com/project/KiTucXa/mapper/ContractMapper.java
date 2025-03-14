package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.ContractDto;
import com.project.KiTucXa.dto.response.ContractResponse;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.dto.update.ContractUpdateDto;
import com.project.KiTucXa.entity.Bill;
import com.project.KiTucXa.entity.Contract;
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
