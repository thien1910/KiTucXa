package com.project.KiTucXa.service;

import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.update.StudentUpdateDto;
import com.project.KiTucXa.entity.Student;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.mapper.StudentMapper;
import com.project.KiTucXa.repository.StudentRepository;
import com.project.KiTucXa.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private UserRepository userRepository;

    public StudentResponse createStudent(StudentDto studentDto) {
        if (studentRepository.existsByMaSinhVien(studentDto.getMaSinhVien())) {
            throw new AppException(ErrorCode.STUDENT_EXITED);
        }

        // Tìm User từ userId
        User user = userRepository.findById(studentDto.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Sử dụng Mapper để chuyển đổi
        Student student = studentMapper.toStudent(studentDto, user);

        // Lưu vào database
        studentRepository.save(student);

        return studentMapper.toStudentResponse(student);
    }

    public List<Student> getStudent() {
        return studentRepository.findAll();
    }

    public StudentResponse getStudent(String studentId) {
        return studentMapper.toStudentResponse(studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found")));
    }

    public StudentResponse updateStudent(String studentId, StudentUpdateDto studentDto) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        studentMapper.updateStudent(student, studentDto);

        return studentMapper.toStudentResponse(studentRepository.save(student));

    }

    public void deleteStudent(String studentId) {
        studentRepository.deleteById(studentId);
    }
}
