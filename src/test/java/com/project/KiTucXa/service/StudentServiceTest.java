package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.project.KiTucXa.Dto.Request.StudentDto;
import com.project.KiTucXa.Dto.Response.StudentResponse;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.StudentUpdateDto;
import com.project.KiTucXa.Entity.Student;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.StudentMapper;
import com.project.KiTucXa.Repository.StudentRepository;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentDto studentDto;
    private Student student;
    private User user;
    private StudentResponse studentResponse;
    private StudentUpdateDto studentUpdateDto;

    @BeforeEach
    void setUp() {
        // Khởi tạo User
        user = new User();
        user.setUserId("user123");
        user.setUserName("testuser");

        // Khởi tạo Student
        student = new Student();
        student.setStudentId("student123");
        student.setMaSinhVien("MSV001");
        student.setUser(user);

        // Khởi tạo DTO
        studentDto = new StudentDto("user123", "MSV001");

        // Khởi tạo Response
        studentResponse = new StudentResponse();
        studentResponse.setStudentId("student123");
        studentResponse.setUserId("user123");
        studentResponse.setMaSinhVien("MSV001");
        studentResponse.setUser(new UserResponse());

        // Khởi tạo Update DTO (giả sử có trường nào cần update)
        studentUpdateDto = new StudentUpdateDto();
        // Bạn có thể thêm các field cần update nếu có
    }

    @Test
    void testCreateStudent_Success() {
        // Kiểm tra student chưa tồn tại
        when(studentRepository.existsByMaSinhVien("MSV001")).thenReturn(false);
        // Tìm User theo userId
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        // Mapping từ DTO sang Student
        when(studentMapper.toStudent(studentDto, user)).thenReturn(student);
        // Lưu student
        when(studentRepository.save(student)).thenReturn(student);
        // Mapping sang StudentResponse
        when(studentMapper.toStudentResponse(student, user)).thenReturn(studentResponse);

        StudentResponse result = studentService.createStudent(studentDto);

        assertNotNull(result);
        assertEquals("student123", result.getStudentId());
        verify(studentRepository).existsByMaSinhVien("MSV001");
        verify(userRepository).findById("user123");
        verify(studentRepository).save(student);
    }

    @Test
    void testCreateStudent_StudentAlreadyExisted() {
        // Nếu student đã tồn tại
        when(studentRepository.existsByMaSinhVien("MSV001")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> studentService.createStudent(studentDto));
        assertEquals(ErrorCode.STUDENT_EXITED, ex.getErrorCode());
    }

    @Test
    void testGetAllStudents() {
        // Giả sử có 1 student trong danh sách
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));
        // Giả sử khi tìm user theo id luôn trả về user
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        // Mapping cho studentResponse
        when(studentMapper.toStudentResponse(student, user)).thenReturn(studentResponse);

        List<StudentResponse> responses = studentService.getAllStudents();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(studentRepository).findAll();
    }

    @Test
    void testGetStudent_Success() {
        when(studentRepository.findById("student123")).thenReturn(Optional.of(student));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(studentMapper.toStudentResponse(student, user)).thenReturn(studentResponse);

        StudentResponse result = studentService.getStudent("student123");

        assertNotNull(result);
        assertEquals("student123", result.getStudentId());
        verify(studentRepository).findById("student123");
    }

    @Test
    void testGetStudent_NotFound() {
        when(studentRepository.findById("student123")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> studentService.getStudent("student123"));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testUpdateStudent_Success() {
        when(studentRepository.findById("student123")).thenReturn(Optional.of(student));
        // Giả sử update mapper cập nhật student theo DTO
        doNothing().when(studentMapper).updateStudent(student, studentUpdateDto);
        when(studentRepository.save(student)).thenReturn(student);
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(studentMapper.toStudentResponse(student, user)).thenReturn(studentResponse);

        StudentResponse result = studentService.updateStudent("student123", studentUpdateDto);

        assertNotNull(result);
        assertEquals("student123", result.getStudentId());
        verify(studentRepository).findById("student123");
        verify(studentRepository).save(student);
    }

    @Test
    void testUpdateStudent_NotFound() {
        when(studentRepository.findById("student123")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> studentService.updateStudent("student123", studentUpdateDto));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById("student123")).thenReturn(true);
        doNothing().when(studentRepository).deleteById("student123");

        assertDoesNotThrow(() -> studentService.deleteStudent("student123"));
        verify(studentRepository).deleteById("student123");
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById("student123")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> studentService.deleteStudent("student123"));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testGetMyInfoStudent_Success() {
        // Thiết lập SecurityContext với username
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Tìm User theo userName
        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));
        // Tìm Student theo user
        when(studentRepository.findByUser(user)).thenReturn(Optional.of(student));
        // Mapping sang StudentResponse
        when(studentMapper.toStudentResponse(student, user)).thenReturn(studentResponse);

        StudentResponse result = studentService.getMyInfoStudent();
        assertNotNull(result);
        assertEquals("student123", result.getStudentId());

        verify(userRepository).findByuserName("testuser");
        verify(studentRepository).findByUser(user);
    }

    @Test
    void testGetMyInfoStudent_UserNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByuserName("nonexistent")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> studentService.getMyInfoStudent());
        assertEquals(ErrorCode.USER_NOT_EXITED, ex.getErrorCode());
    }

    @Test
    void testGetMyInfoStudent_StudentNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByuserName("testuser")).thenReturn(Optional.of(user));
        when(studentRepository.findByUser(user)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> studentService.getMyInfoStudent());
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }
}
