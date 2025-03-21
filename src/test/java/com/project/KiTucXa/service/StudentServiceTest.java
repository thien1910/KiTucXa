package com.project.KiTucXa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.update.StudentUpdateDto;
import com.project.KiTucXa.entity.Student;
import com.project.KiTucXa.mapper.StudentMapper;
import com.project.KiTucXa.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentDto studentDto;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        student = new Student();
        studentDto = new StudentDto();
        studentResponse = new StudentResponse();
    }

    @Test
    void createStudent_ShouldReturnSavedStudent() {
        when(studentRepository.existsByMaSinhVien(studentDto.getMaSinhVien())).thenReturn(false);
        when(studentMapper.toStudent(studentDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.createStudent(studentDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void getStudent_WhenStudentExists_ShouldReturnStudent() {
        when(studentRepository.findById("1")).thenReturn(Optional.of(student));
        when(studentMapper.toStudentResponse(student)).thenReturn(studentResponse);

        StudentResponse result = studentService.getStudent("1");

        assertNotNull(result);
        verify(studentRepository, times(1)).findById("1");
    }

    @Test
    void getStudent_WhenStudentNotExists_ShouldThrowException() {
        when(studentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudent("1"));
    }

    @Test
    void updateStudent_WhenStudentExists_ShouldReturnUpdatedStudent() {
        StudentUpdateDto updateDto = new StudentUpdateDto();

        when(studentRepository.findById("1")).thenReturn(Optional.of(student));
        doNothing().when(studentMapper).updateStudent(student, updateDto);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toStudentResponse(student)).thenReturn(studentResponse);

        StudentResponse result = studentService.updateStudent("1", updateDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void deleteStudent_WhenStudentExists_ShouldDeleteStudent() {
        doNothing().when(studentRepository).deleteById("1");

        assertDoesNotThrow(() -> studentService.deleteStudent("1"));
        verify(studentRepository, times(1)).deleteById("1");
    }
}
