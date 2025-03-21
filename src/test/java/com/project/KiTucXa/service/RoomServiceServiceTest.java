import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.RoomServiceDto;
import com.project.KiTucXa.dto.response.RoomServiceResponse;
import com.project.KiTucXa.dto.update.RoomServiceUpdateDto;
import com.project.KiTucXa.entity.RoomService;
import com.project.KiTucXa.mapper.RoomServiceMapper;
import com.project.KiTucXa.repository.RoomServiceRepository;
import com.project.KiTucXa.service.RoomServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoomServiceServiceTest {

    @Mock
    private RoomServiceRepository roomServiceRepository;

    @Mock
    private RoomServiceMapper roomServiceMapper;

    @InjectMocks
    private RoomServiceService roomServiceService;

    private RoomService roomService;
    private RoomServiceDto roomServiceDto;
    private RoomServiceResponse roomServiceResponse;

    @BeforeEach
    void setUp() {
        roomService = new RoomService();
        roomServiceDto = new RoomServiceDto();
        roomServiceResponse = new RoomServiceResponse();
    }

    @Test
    void getAllRoomService_ShouldReturnList() {
        when(roomServiceRepository.findAll()).thenReturn(Arrays.asList(roomService));
        when(roomServiceMapper.toRoomServiceResponse(any())).thenReturn(roomServiceResponse);

        List<RoomServiceResponse> result = roomServiceService.getAllRoomService();

        assertEquals(1, result.size());
        verify(roomServiceRepository, times(1)).findAll();
    }

    @Test
    void getRoomService_WhenExists_ShouldReturnRoomService() {
        when(roomServiceRepository.findById("1")).thenReturn(Optional.of(roomService));
        when(roomServiceMapper.toRoomServiceResponse(roomService)).thenReturn(roomServiceResponse);

        RoomServiceResponse result = roomServiceService.getRoomService("1");

        assertNotNull(result);
        verify(roomServiceRepository, times(1)).findById("1");
    }

    @Test
    void getRoomService_WhenNotExists_ShouldThrowException() {
        when(roomServiceRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomServiceService.getRoomService("1"));
    }

    @Test
    void createRoomService_ShouldReturnSavedRoomService() {
        when(roomServiceMapper.toRoomService(roomServiceDto)).thenReturn(roomService);
        when(roomServiceRepository.save(roomService)).thenReturn(roomService);

        RoomService result = roomServiceService.createRoomService(roomServiceDto);

        assertNotNull(result);
        verify(roomServiceRepository, times(1)).save(roomService);
    }

    @Test
    void updateRoomService_WhenExists_ShouldReturnUpdatedRoomService() {
        RoomServiceUpdateDto updateDto = new RoomServiceUpdateDto();

        when(roomServiceRepository.findById("1")).thenReturn(Optional.of(roomService));
        doNothing().when(roomServiceMapper).updateRoomService(roomService, updateDto);
        when(roomServiceRepository.save(roomService)).thenReturn(roomService);
        when(roomServiceMapper.toRoomServiceResponse(roomService)).thenReturn(roomServiceResponse);

        RoomServiceResponse result = roomServiceService.updateRoomService("1", updateDto);

        assertNotNull(result);
        verify(roomServiceRepository, times(1)).save(roomService);
    }

    @Test
    void deleteRoomService_WhenExists_ShouldDeleteRoomService() {
        when(roomServiceRepository.existsById("1")).thenReturn(true);
        doNothing().when(roomServiceRepository).deleteById("1");

        assertDoesNotThrow(() -> roomServiceService.deleteRoomService("1"));
        verify(roomServiceRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteRoomService_WhenNotExists_ShouldThrowException() {
        when(roomServiceRepository.existsById("1")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roomServiceService.deleteRoomService("1"));
    }
}