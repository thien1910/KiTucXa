package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.BillDto;
import com.project.KiTucXa.dto.response.BillResponse;
import com.project.KiTucXa.dto.update.BillUpdateDto;
import com.project.KiTucXa.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(source = "contractId", target = "contract.contractId")
    Bill toBill(BillDto billDto);

    @Mapping(source = "contract.contractId", target = "contractId")
    BillResponse toBillResponse(Bill bill);
    void updateBill(@MappingTarget Bill bill, BillUpdateDto billDto);
}

