package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.RoomDto;
import com.project.KiTucXa.Dto.Response.RoomResponse;
import com.project.KiTucXa.Dto.Update.RoomUpdateDto;
import com.project.KiTucXa.Entity.Room;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.RoomMapper;
import com.project.KiTucXa.Repository.RoomRepository;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.RoomService;
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
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private User user;
    private RoomResponse roomResponse;
    private RoomDto roomDto;
    private RoomUpdateDto roomUpdateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("1");

        room = new Room();
        room.setRoomId("1");
        room.setRoomName("A101");
        room.setRoomPrice(BigDecimal.valueOf(500));
        room.setUser(user);

        roomResponse = new RoomResponse();
        roomResponse.setRoomId("1");
        roomResponse.setRoomName("A101");

        roomDto = new RoomDto("1", "A101", "IT", 4, 0, "Standard", BigDecimal.valueOf(500), null, "Note");
        roomUpdateDto = new RoomUpdateDto("1", "A102", "IT", 4, 2, "Deluxe", BigDecimal.valueOf(600), null, "Updated Note");
    }

    @Test
    void testCreateRoom_Success() {
        when(roomRepository.findByRoomName(any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(roomMapper.toRoom(any(), any())).thenReturn(room);
        when(roomRepository.save(any())).thenReturn(room);
        when(roomMapper.toRoomResponse(any())).thenReturn(roomResponse);

        RoomResponse result = roomService.createRoom(roomDto);

        assertNotNull(result);
        assertEquals("1", result.getRoomId());
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void testCreateRoom_AlreadyExists() {
        when(roomRepository.findByRoomName(any())).thenReturn(Optional.of(room));

        AppException exception = assertThrows(AppException.class, () -> roomService.createRoom(roomDto));
        assertEquals(ErrorCode.ROOM_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void testGetAllRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(roomMapper.toRoomResponse(any())).thenReturn(roomResponse);

        List<RoomResponse> result = roomService.getAllRoom();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetRoomById_Success() {
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(roomMapper.toRoomResponse(any())).thenReturn(roomResponse);

        RoomResponse result = roomService.getRoom("1");

        assertNotNull(result);
        assertEquals("1", result.getRoomId());
    }

    @Test
    void testGetRoomById_NotFound() {
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.getRoom("1"));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testUpdateRoom_Success() {
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);
        when(roomMapper.toRoomResponse(any())).thenReturn(roomResponse);

        RoomResponse result = roomService.updateRoom("1", roomUpdateDto);

        assertNotNull(result);
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRoom_NotFound() {
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.updateRoom("1", roomUpdateDto));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testDeleteRoom_Success() {
        when(roomRepository.existsById(any())).thenReturn(true);
        doNothing().when(roomRepository).deleteById(any());

        roomService.deleteRoom("1");

        verify(roomRepository, times(1)).deleteById(any());
    }

    @Test
    void testDeleteRoom_NotFound() {
        when(roomRepository.existsById(any())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.deleteRoom("1"));
        assertEquals("Room not found", exception.getMessage());
    }
}
