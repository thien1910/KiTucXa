package com.project.KiTucXa.service;

import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.response.UserResponse;
import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.mapper.UserMapper;
import com.project.KiTucXa.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;


    public User createUser(UserDto userDto){


        if (userRepository.existsByuserName(userDto.getUserName()))
            throw new AppException(ErrorCode.USER_EXITED);

        User user = userMapper.toUser(userDto);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassWord(passwordEncoder.encode(userDto.getPassWord()));

        return userRepository.save(user);


    }

    public List<User> getUser() {
        return userRepository.findAll();
    }

    public UserResponse getUser (String userId ) {

        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found")));

    }
    public UserResponse updateUser(String userId, UserUpdateDto userDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));

        userMapper.updateUser(user, userDto);

        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
