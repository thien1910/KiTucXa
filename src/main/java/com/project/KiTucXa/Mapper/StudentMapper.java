package com.project.KiTucXa.Mapper;

import com.project.KiTucXa.Dto.Request.StudentDto;
import com.project.KiTucXa.Dto.Response.StudentResponse;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.StudentUpdateDto;
import com.project.KiTucXa.Entity.Student;
import com.project.KiTucXa.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "user", source = "user")
    Student toStudent(StudentDto studentDto, User user);

    @Mapping(target = "userId", source = "user.userId")
    StudentResponse toStudentResponse(Student student, User user);

    void updateStudent(@MappingTarget Student student, StudentUpdateDto studentDto);

    @Named("toUserResponse")
    default UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .passWord(user.getPassWord())
                .fullName(user.getFullName())
                .gender(user.getGender())
//                .roomNameStudent(user.getRoomNameStudent())
                .cccd(user.getCccd())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
