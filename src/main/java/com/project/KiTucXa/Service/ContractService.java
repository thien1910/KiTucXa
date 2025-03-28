package com.project.KiTucXa.Service;

import com.project.KiTucXa.Dto.Request.ContractDto;
import com.project.KiTucXa.Dto.Response.ContractResponse;
import com.project.KiTucXa.Dto.Update.ContractUpdateDto;
import com.project.KiTucXa.Entity.Contract;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.ContractStatus;
import com.project.KiTucXa.Enum.RoomStatus;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.ContractMapper;
import com.project.KiTucXa.Repository.ContractRepository;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        // Kiểm tra xem phòng đã đầy người chưa
        if (room.getCurrentOccupancy() >= room.getMaximumOccupancy()) {
            throw new AppException(ErrorCode.ROOM_FULL);
        }

        // Kiểm tra xem user đã có hợp đồng hiệu lực chưa
        List<Contract> activeContracts = contractRepository.findByUser_UserId(user.getUserId())
                .stream()
                .filter(c -> {
                    boolean isActive = c.getContractStatus() == ContractStatus.Active;
                    boolean isNotExpired = c.getEndDate() != null && c.getEndDate().after(new Date());
                    System.out.println("Contract ID: " + c.getContractId() +
                            ", Status: " + c.getContractStatus() +
                            ", EndDate: " + c.getEndDate() +
                            ", isActive: " + isActive +
                            ", isNotExpired: " + isNotExpired);
                    return isActive && isNotExpired;
                })
                .collect(Collectors.toList());

        if (!activeContracts.isEmpty()) {
            throw new AppException(ErrorCode.USER_HAS_ACTIVE_CONTRACT);
        }

        Contract contract = contractMapper.toContract(contractDto);
        contract.setUser(user);
        contract.setRoom(room);

        // Lưu hợp đồng
        contractRepository.save(contract);

        // Tăng currentOccupancy của phòng lên 1
        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);

        // Nếu sau khi tăng, currentOccupancy bằng với maximumOccupancy thì cập nhật trạng thái phòng thành FULL
        if (room.getCurrentOccupancy() == room.getMaximumOccupancy()) {
            room.setRoomStatus(RoomStatus.full_room);
        }

        roomRepository.save(room);

        return contractMapper.toContractResponse(contract);
    }




    public List<ContractResponse> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        Date now = new Date();

        for (Contract contract : contracts) {
            if (contract.getContractStatus() == ContractStatus.Active && contract.getEndDate().before(now)) {
                contract.setContractStatus(ContractStatus.Inactive);
                contractRepository.save(contract);
            }
        }

        return contracts.stream()
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
        // Lấy hợp đồng cần xóa
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Lấy phòng liên quan từ hợp đồng
        Room room = contract.getRoom();

        // Xóa hợp đồng
        contractRepository.deleteById(contractId);

        // Giảm currentOccupancy của phòng đi 1 nếu có giá trị > 0
        if (room.getCurrentOccupancy() > 0) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);

            // Nếu phòng trước đó ở trạng thái FULL và sau khi giảm số lượng người mà chưa đạt max thì cập nhật lại trạng thái phòng (ví dụ: AVAILABLE)
            if (room.getCurrentOccupancy() < room.getMaximumOccupancy() && room.getRoomStatus() == RoomStatus.full_room) {
                room.setRoomStatus(RoomStatus.empty_room); // Giả sử bạn có định nghĩa RoomStatus.AVAILABLE
            }

            roomRepository.save(room);
        }
    }

    public List<Contract> getContractsByUserId(String userId) {
        return contractRepository.findByUser_UserId(userId);
    }
    @Scheduled(cron = "0 0 0 * * ?") // Mỗi ngày lúc 00:00
    @Transactional
    public void updateExpiredContracts() {
        Date now = new Date();
        List<Contract> expiredContracts = contractRepository.findByEndDateBeforeAndContractStatus(now, ContractStatus.Active);

        for (Contract contract : expiredContracts) {
            contract.setContractStatus(ContractStatus.Inactive);

            // Lấy phòng liên quan và giảm currentOccupancy xuống 1 nếu có giá trị > 0
            Room room = contract.getRoom();
            if (room.getCurrentOccupancy() > 0) {
                room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                roomRepository.save(room);
            }
        }

        contractRepository.saveAll(expiredContracts);
        System.out.println("Updated " + expiredContracts.size() + " expired contracts to INACTIVE.");
    }

}
