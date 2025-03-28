package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.RoomServiceDto;
import com.project.KiTucXa.Dto.Response.RoomServiceResponse;
import com.project.KiTucXa.Dto.Update.RoomServiceUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.RoomService;
import com.project.KiTucXa.Entity.UtilityService;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.RoomServiceMapper;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.RoomServiceRepository;
import com.project.KiTucXa.Repository.UtilityServiceRepository;
import com.project.KiTucXa.Service.RoomServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceServiceTest {

    @Mock
    private RoomServiceRepository roomServiceRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UtilityServiceRepository utilityServiceRepository;

    @Mock
    private RoomServiceMapper roomServiceMapper;

    @InjectMocks
    private RoomServiceService roomServiceService;

    private Room room;
    private UtilityService utilityService;
    private RoomService roomService;
    private RoomServiceDto roomServiceDto;
    private RoomServiceUpdateDto roomServiceUpdateDto;
    private RoomServiceResponse roomServiceResponse;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setRoomId("1");

        utilityService = new UtilityService();
        utilityService.setUtilityServiceId("1");

        roomService = new RoomService();
        roomService.setRoomServiceId("1");
        roomService.setRoom(room);
        roomService.setUtilityService(utilityService);
        roomService.setPrice(BigDecimal.valueOf(1000));

        roomServiceDto = new RoomServiceDto("1", "1", BigDecimal.valueOf(1000));
        roomServiceUpdateDto = new RoomServiceUpdateDto(BigDecimal.valueOf(1500));

        roomServiceResponse = new RoomServiceResponse();
        roomServiceResponse.setRoomServiceId("1"); // Đảm bảo giá trị này được set
        // Nếu có các field khác, bạn cũng nên set giá trị cho chúng.
    }


    @Test
    void testCreateRoomService_Success() {
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(utilityServiceRepository.findById(any())).thenReturn(Optional.of(utilityService));
        when(roomServiceMapper.toRoomService(any(), any(), any())).thenReturn(roomService);
        when(roomServiceRepository.save(any())).thenReturn(roomService);
        when(roomServiceMapper.toRoomServiceResponse(any())).thenReturn(roomServiceResponse);

        RoomServiceResponse result = roomServiceService.createRoomService(roomServiceDto);

        assertNotNull(result);
        assertEquals("1", result.getRoomServiceId());
        verify(roomServiceRepository, times(1)).save(any());
    }

    @Test
    void testGetAllRoomServices() {
        when(roomServiceRepository.findAll()).thenReturn(List.of(roomService));
        when(roomServiceMapper.toRoomServiceResponse(any())).thenReturn(roomServiceResponse);

        List<RoomServiceResponse> result = roomServiceService.getAllRoomServices();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetRoomServiceById_Success() {
        when(roomServiceRepository.findById(any())).thenReturn(Optional.of(roomService));
        when(roomServiceMapper.toRoomServiceResponse(any())).thenReturn(roomServiceResponse);

        RoomServiceResponse result = roomServiceService.getRoomServiceById("1");

        assertNotNull(result);
        assertEquals("1", result.getRoomServiceId());
    }

    @Test
    void testUpdateRoomService_Success() {
        when(roomServiceRepository.findById(any())).thenReturn(Optional.of(roomService));
        when(roomServiceRepository.save(any())).thenReturn(roomService);
        when(roomServiceMapper.toRoomServiceResponse(any())).thenReturn(roomServiceResponse);

        RoomServiceResponse result = roomServiceService.updateRoomService("1", roomServiceUpdateDto);

        assertNotNull(result);
        verify(roomServiceRepository, times(1)).save(any());
    }

    @Test
    void testDeleteRoomService_Success() {
        when(roomServiceRepository.existsById(any())).thenReturn(true);
        doNothing().when(roomServiceRepository).deleteById(any());

        roomServiceService.deleteRoomService("1");

        verify(roomServiceRepository, times(1)).deleteById(any());
    }
}
