package com.project.KiTucXa.Mapper;


import com.project.KiTucXa.Dto.Request.BillDetailDto;
import com.project.KiTucXa.Dto.Response.BillDetailResponse;
import com.project.KiTucXa.Dto.Update.BillDetailUpdateDto;
import com.project.KiTucXa.Entity.BillDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BillDetailMapper {
    @Mapping(source = "billId", target = "bill.billId")
    @Mapping(source = "utilityServiceId", target = "utilityService.utilityServiceId")
    BillDetail toBillDetail(BillDetailDto billDetailDto);

    @Mapping(source = "bill.billId", target = "billId")
    @Mapping(source = "utilityService.utilityServiceId", target = "utilityServiceId")
    BillDetailResponse toBillDetailResponse(BillDetail billDetail);
    void updateBillDetail(@MappingTarget BillDetail billDetail, BillDetailUpdateDto billDetailDto);
}

