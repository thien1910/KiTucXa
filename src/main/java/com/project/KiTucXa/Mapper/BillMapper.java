package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.BillDto;
import com.project.KiTucXa.Dto.Response.BillResponse;
import com.project.KiTucXa.Dto.Update.BillUpdateDto;
import com.project.KiTucXa.Entity.Bill;
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

