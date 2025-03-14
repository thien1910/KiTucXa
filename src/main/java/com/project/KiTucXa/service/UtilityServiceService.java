package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.UtilityServiceDto;
import com.project.KiTucXa.dto.response.UtilityServiceResponse;
import com.project.KiTucXa.dto.update.UtilityServiceUpdateDto;
import com.project.KiTucXa.entity.UtilityService;
import com.project.KiTucXa.mapper.UtilityServiceMapper;
import com.project.KiTucXa.repository.UtilityServiceRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilityServiceService {
    @Autowired
    private UtilityServiceRepository utilityServiceRepository;
    @Autowired
    private UtilityServiceMapper utilityServiceMapper;

    public UtilityServiceResponse createUtilityService(UtilityServiceDto utilityServiceDto) {
        UtilityService utilityService = utilityServiceMapper.toUtilityService(utilityServiceDto);
        utilityServiceRepository.save(utilityService);
        return utilityServiceMapper.toUtilityServiceResponse(utilityService);
    }

    public List<UtilityServiceResponse> getAllUtilityServices() {
        return utilityServiceRepository.findAll().stream()
                .map(utilityServiceMapper::toUtilityServiceResponse)
                .collect(Collectors.toList());
    }

    public UtilityServiceResponse getUtilityServiceById(String utilityServiceId) {
        UtilityService utilityService = utilityServiceRepository.findById(utilityServiceId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_SERVICE_NOT_FOUND));

        return utilityServiceMapper.toUtilityServiceResponse(utilityService);
    }

    public void deleteUtilityService(String utilityServiceId) {
        if (!utilityServiceRepository.existsById(utilityServiceId)) {
            throw new AppException(ErrorCode.UTILITY_SERVICE_NOT_FOUND);
        }
        utilityServiceRepository.deleteById(utilityServiceId);
    }

    public UtilityServiceResponse updateUtilityService(
            String utilityServiceId,
            UtilityServiceUpdateDto utilityServiceDto) {
        UtilityService utilityService = utilityServiceRepository.findById(utilityServiceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        utilityServiceMapper.updateUtilityService(utilityService, utilityServiceDto);
        utilityService = utilityServiceRepository.save(utilityService);
        return utilityServiceMapper.toUtilityServiceResponse(utilityService);
    }

}
