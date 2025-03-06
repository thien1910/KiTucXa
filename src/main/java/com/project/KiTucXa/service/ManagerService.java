package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.ManagerDto;
import com.project.KiTucXa.dto.response.ManagerResponse;
import com.project.KiTucXa.dto.update.ManagerUpdateDto;
import com.project.KiTucXa.entity.Manager;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.mapper.ManagerMapper;
import com.project.KiTucXa.repository.ManagerRepository;
import com.project.KiTucXa.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private UserRepository userRepository;
    public List<ManagerResponse> getAllManager() {
        return managerRepository.findAll().stream()
                .map(managerMapper::toManagerResponse)
                .collect(Collectors.toList());
    }

    public ManagerResponse getManager(String managerId) {
        return managerRepository.findById(managerId)
                .map(managerMapper::toManagerResponse)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }

    public ManagerResponse createManager(ManagerDto managerDto) {
        if (managerRepository.existsByDepartment(managerDto.getDepartment())) {
            throw new AppException(ErrorCode.MANAGER_EXITED);
        }

        User user = userRepository.findById(managerDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Manager manager = managerMapper.toManager(managerDto, user);
        managerRepository.save(manager);

        return managerMapper.toManagerResponse(manager);
    }

    public ManagerResponse updateManager(String managerId, ManagerUpdateDto managerDto) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        managerMapper.updateManager(manager, managerDto);
        manager = managerRepository.save(manager);
        return managerMapper.toManagerResponse(manager);
    }

    public void deleteManager(String managerId) {
        if (!managerRepository.existsById(managerId)) {
            throw new RuntimeException("Room not found");
        }
        managerRepository.deleteById(managerId);
    }
}
