package com.project.KiTucXa.Service;

import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.ContractMapper;
import com.project.KiTucXa.Repository.ContractRepository;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public ContractResponse createContract(ContractDto contractDto) {
        User user = userRepository.findById(contractDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Room room = roomRepository.findById(contractDto.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Contract contract = contractMapper.toContract(contractDto);
        contract.setUser(user);
        contract.setRoom(room);

        contractRepository.save(contract);
        return contractMapper.toContractResponse(contract);
    }

    public List<ContractResponse> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toContractResponse)
                .collect(Collectors.toList());
    }

    public ContractResponse getContractById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        return contractMapper.toContractResponse(contract);
    }
    public ContractResponse updateContract(String contractId, ContractUpdateDto contractDto) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(()-> new RuntimeException("Contract not found"));

        contractMapper.updateContract(contract, contractDto);

        return contractMapper.toContractResponse(contractRepository.save(contract));

    }

    public void deleteContract(String contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        contractRepository.deleteById(contractId);
    }
    public List<Contract> getContractsByUserId(String userId) {
        return contractRepository.findByUser_UserId(userId);
    }
}
