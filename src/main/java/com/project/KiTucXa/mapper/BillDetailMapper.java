package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.BillDetailDto;
import com.project.KiTucXa.dto.response.BillDetailResponse;
import com.project.KiTucXa.dto.update.BillDetailUpdateDto;
import com.project.KiTucXa.entity.BillDetail;
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

