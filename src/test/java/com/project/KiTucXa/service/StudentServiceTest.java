package com.project.KiTucXa.service;

import com.project.KiTucXa.Dto.Request.StudentDto;
import com.project.KiTucXa.Dto.Response.StudentResponse;
import com.project.KiTucXa.Dto.Update.StudentUpdateDto;
import com.project.KiTucXa.Entity.Student;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.StudentMapper;
import com.project.KiTucXa.Repository.StudentRepository;
import com.project.KiTucXa.Repository.UserRepository;
import com.project.KiTucXa.Service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private Student student;
    private User user;
    private StudentResponse studentResponse;
    private StudentDto studentDto;
    private StudentUpdateDto studentUpdateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("1");

        student = new Student();
        student.setStudentId("1");
        student.setMaSinhVien("SV001");
        student.setUser(user);

        studentResponse = new StudentResponse();
        studentResponse.setStudentId("1");
        studentResponse.setMaSinhVien("SV001");

        studentDto = new StudentDto("1", "SV001");
        studentUpdateDto = new StudentUpdateDto("SV002");
    }

    @Test
    void testCreateStudent_Success() {
        when(studentRepository.existsByMaSinhVien(any())).thenReturn(false);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(studentMapper.toStudent(any(), any())).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(studentMapper.toStudentResponse(any(), any())).thenReturn(studentResponse);

        StudentResponse result = studentService.createStudent(studentDto);

        assertNotNull(result);
        assertEquals("1", result.getStudentId());
        verify(studentRepository, times(1)).save(any());
    }

    @Test
    void testCreateStudent_AlreadyExists() {
        when(studentRepository.existsByMaSinhVien(any())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> studentService.createStudent(studentDto));
        assertEquals(ErrorCode.STUDENT_EXITED, exception.getErrorCode());
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(studentMapper.toStudentResponse(any(), any())).thenReturn(studentResponse);

        List<StudentResponse> result = studentService.getAllStudents();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));
        when(studentMapper.toStudentResponse(any(), any())).thenReturn(studentResponse);

        StudentResponse result = studentService.getStudent("1");

        assertNotNull(result);
        assertEquals("1", result.getStudentId());
    }

    @Test
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> studentService.getStudent("1"));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateStudent_Success() {
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));
        when(studentRepository.save(any())).thenReturn(student);
        when(studentMapper.toStudentResponse(any(), any())).thenReturn(studentResponse);

        StudentResponse result = studentService.updateStudent("1", studentUpdateDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any());
    }

    @Test
    void testUpdateStudent_NotFound() {
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> studentService.updateStudent("1", studentUpdateDto));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(any())).thenReturn(true);
        doNothing().when(studentRepository).deleteById(any());

        studentService.deleteStudent("1");

        verify(studentRepository, times(1)).deleteById(any());
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(any())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> studentService.deleteStudent("1"));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }
}
