package com.project.KiTucXa.Service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserRepository userRepository;

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

        return studentMapper.toStudentResponse(student, user);
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    User user = userRepository.findById(student.getUser().getUserId()).orElse(null);
                    return studentMapper.toStudentResponse(student, user);
                })
                .collect(Collectors.toList());
    }

    public StudentResponse getStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        User user = userRepository.findById(student.getUser().getUserId()).orElse(null);

        return studentMapper.toStudentResponse(student, user);
    }

    public StudentResponse updateStudent(String studentId, StudentUpdateDto studentDto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        studentMapper.updateStudent(student, studentDto);
        studentRepository.save(student);

        User user = userRepository.findById(student.getUser().getUserId()).orElse(null);

        return studentMapper.toStudentResponse(student, user);
    }

    public void deleteStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentRepository.deleteById(studentId);
    }
    public StudentResponse getMyInfoStudent() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByuserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));

        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return studentMapper.toStudentResponse(student, user);
    }
}
