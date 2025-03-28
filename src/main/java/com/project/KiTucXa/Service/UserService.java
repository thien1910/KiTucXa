package com.project.KiTucXa.Service;

import com.project.KiTucXa.Dto.Request.UserCreationRequest;
import com.project.KiTucXa.Dto.Response.UserResponse;
import com.project.KiTucXa.Dto.Update.PasswordUpdateDto;
import com.project.KiTucXa.Dto.Update.UserUpdateRequest;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Role;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Mapper.UserMapper;
import com.project.KiTucXa.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Data

@NoArgsConstructor
@AllArgsConstructor
@Slf4j

public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    // tự hiểu tạo tài khoản mới là tài khoản cho student

    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.existsByuserName(request.getUserName()))
            throw new AppException(ErrorCode.USER_EXITED);

        User user = userMapper.toUser(request);
        user.setPassWord(passwordEncoder.encode(request.getPassWord()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.STUDENT.name());

        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }
    // chỉ cần chuyển vào token sẽ lấy đc thông tin của chính mk
    public UserResponse getMyInfo(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByuserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));
        return userMapper.toUserResponse(user);
    }

    public void changePassword(PasswordUpdateDto request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByuserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassWord())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }





}
