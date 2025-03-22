package com.project.KiTucXa.Controller;


import jakarta.validation.Valid;
import com.project.KiTucXa.Dto.Request.ApiResponse;
import com.project.KiTucXa.Dto.Request.StudentDto;
import com.project.KiTucXa.Dto.Response.StudentResponse;
import com.project.KiTucXa.Dto.Update.StudentUpdateDto;
import com.project.KiTucXa.Service.StudentService;
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
    List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents();
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
    @GetMapping("/my-infoStudent")
    public StudentResponse getMyInfoStudent() {
        return studentService.getMyInfoStudent();
    }
}
