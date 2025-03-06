package com.project.KiTucXa.mapper;

import com.project.KiTucXa.dto.request.StudentDto;
import com.project.KiTucXa.dto.response.StudentResponse;
import com.project.KiTucXa.dto.update.StudentUpdateDto;
import com.project.KiTucXa.entity.Student;
import com.project.KiTucXa.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface StudentMapper {
    // Chuyển từ StudentDto sang Student (cần User)
    @Mapping(target = "user", source = "user") // Đảm bảo User được truyền vào
    Student toStudent(StudentDto studentDto, User user);

    // Chuyển từ Student sang StudentResponse
    @Mapping(target = "userId", source = "user.userId") // Trả về userId thay vì User object
    StudentResponse toStudentResponse(Student student);

    // Cập nhật Student từ StudentUpdateDto
    void updateStudent(@MappingTarget Student student, StudentUpdateDto studentUpdateDto);
}
