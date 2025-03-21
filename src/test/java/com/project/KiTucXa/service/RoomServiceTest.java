package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.RoomDto;
import com.project.KiTucXa.dto.response.RoomResponse;
import com.project.KiTucXa.dto.update.RoomUpdateDto;
import com.project.KiTucXa.entity.Room;
import com.project.KiTucXa.mapper.RoomMapper;
import com.project.KiTucXa.repository.RoomRepository;
import com.project.KiTucXa.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private RoomDto roomDto;
    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        room = new Room();
        roomDto = new RoomDto();
        roomResponse = new RoomResponse();
    }

    @Test
    void getAllRooms_ShouldReturnList() {
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room));
        when(roomMapper.toRoomResponse(any())).thenReturn(roomResponse);

        List<RoomResponse> result = roomService.getAllRoom();

        assertEquals(1, result.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getRoom_WhenRoomExists_ShouldReturnRoom() {
        when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        when(roomMapper.toRoomResponse(room)).thenReturn(roomResponse);

        RoomResponse result = roomService.getRoom("1");

        assertNotNull(result);
        verify(roomRepository, times(1)).findById("1");
    }

    @Test
    void getRoom_WhenRoomNotExists_ShouldThrowException() {
        when(roomRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomService.getRoom("1"));
    }

    @Test
    void createRoom_ShouldReturnSavedRoom() {
        when(roomMapper.toRoom(roomDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);

        Room result = roomService.createRoom(roomDto);

        assertNotNull(result);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void updateRoom_WhenRoomExists_ShouldReturnUpdatedRoom() {
        RoomUpdateDto updateDto = new RoomUpdateDto();

        when(roomRepository.findById("1")).thenReturn(Optional.of(room));
        doNothing().when(roomMapper).updateRoom(room, updateDto);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toRoomResponse(room)).thenReturn(roomResponse);

        RoomResponse result = roomService.updateRoom("1", updateDto);

        assertNotNull(result);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void deleteRoom_WhenRoomExists_ShouldDeleteRoom() {
        when(roomRepository.existsById("1")).thenReturn(true);
        doNothing().when(roomRepository).deleteById("1");

        assertDoesNotThrow(() -> roomService.deleteRoom("1"));
        verify(roomRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteRoom_WhenRoomNotExists_ShouldThrowException() {
        when(roomRepository.existsById("1")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roomService.deleteRoom("1"));
    }
}
