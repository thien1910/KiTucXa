package com.project.KiTucXa.controller;

import com.project.KiTucXa.dto.request.ApiResponse;
import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.dto.update.StudentUpdateDto;
import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.entity.Student;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.service.StudentService;
import com.project.KiTucXa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    ApiResponse<StudentResponse> createStudent(@RequestBody @Valid StudentDto studentDto) {
        ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(studentService.createStudent(studentDto));
        return apiResponse;
    }

    @GetMapping("/list")
    List<Student> getStudent() {
        return studentService.getStudent();
    }

    @GetMapping("/{studentId}")
    StudentResponse getStudent(@PathVariable("studentId") String studentId) {
        return studentService.getStudent(studentId);
    }



    @PutMapping("/update/{studentId}")
    StudentResponse updateStudent(@PathVariable String studentId,
            @RequestBody StudentUpdateDto studentDto) {
        return studentService.updateStudent(studentId, studentDto);
    }


    @DeleteMapping("/delete/{studentId}")
    String deleteStudent (@PathVariable String studentId){
        studentService.deleteStudent(studentId);
        return "Student has been detele";
    }
}
