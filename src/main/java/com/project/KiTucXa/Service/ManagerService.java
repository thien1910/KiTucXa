package com.project.KiTucXa.Service;


import com.project.KiTucXa.Dto.Request.ManagerDto;
import com.project.KiTucXa.Dto.Response.ManagerResponse;
import com.project.KiTucXa.Dto.Update.ManagerUpdateDto;
import com.project.KiTucXa.Entity.Manager;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.ManagerMapper;
import com.project.KiTucXa.Repository.ManagerRepository;
import com.project.KiTucXa.Repository.UserRepository;
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

public class ManagerService {
    @Autowired
     ManagerRepository managerRepository;
    @Autowired
     ManagerMapper managerMapper;
    @Autowired
     UserRepository userRepository;

    public List<ManagerResponse> getAllManagers() {
        return managerRepository.findAll().stream()
                .map(manager -> managerMapper.toManagerResponse(manager, manager.getUser()))
                .collect(Collectors.toList());
    }

    public ManagerResponse getManager(String managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new AppException(ErrorCode.MANAGER_NOT_FOUND));
        return managerMapper.toManagerResponse(manager, manager.getUser());
    }

    public ManagerResponse createManager(ManagerDto managerDto) {
        if (managerRepository.existsByDepartment(managerDto.getDepartment())) {
            throw new AppException(ErrorCode.MANAGER_EXITED);
        }

        User user = userRepository.findById(managerDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Manager manager = managerMapper.toManager(managerDto, user);
        manager = managerRepository.save(manager);

        return managerMapper.toManagerResponse(manager, manager.getUser());
    }

    public ManagerResponse updateManager(String managerId, ManagerUpdateDto managerUpdateDto) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new AppException(ErrorCode.MANAGER_NOT_FOUND));

        managerMapper.updateManager(manager, managerUpdateDto);
        manager = managerRepository.save(manager);

        return managerMapper.toManagerResponse(manager, manager.getUser());
    }

    public void deleteManager(String managerId) {
        if (!managerRepository.existsById(managerId)) {
            throw new AppException(ErrorCode.MANAGER_NOT_FOUND);
        }
        managerRepository.deleteById(managerId);
    }
}
