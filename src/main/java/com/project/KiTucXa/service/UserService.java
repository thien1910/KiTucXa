package com.project.KiTucXa.service;

    import com.project.KiTucXa.Comperments.JwtTokenUtil;
    import com.project.KiTucXa.dto.request.UserDto;
import com.project.KiTucXa.dto.update.UserUpdateDto;
import com.project.KiTucXa.dto.response.UserResponse;
    import com.project.KiTucXa.entity.Role;
    import com.project.KiTucXa.entity.User;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.mapper.UserMapper;
    import com.project.KiTucXa.repository.RoleRepository;
    import com.project.KiTucXa.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RoleRepository roleRepository;


    public User createUser(UserDto userDto){
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (userRepository.existsByuserName(userDto.getUserName()))
            throw new AppException(ErrorCode.USER_EXITED);

        User user = userMapper.toUser(userDto);
        user.setPassWord(passwordEncoder.encode(userDto.getPassWord()));
        user.setRole(role);

        return userRepository.save(user);


    }


    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
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
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByuserName(name).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXITED));
        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
    public String login(String userName, String passWord) throws Exception {
        Optional<User> optionalUser = userRepository.findByuserName(userName);
        if (optionalUser.isEmpty()){
            throw new AppException(ErrorCode.INVALID_USERNAME_PASSWORD);
        }
        //return optionalUser.get();
        User existingUser = optionalUser.get();
        // check password
        if (!passwordEncoder.matches(passWord, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong UserName or Password");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userName, passWord,
                existingUser.getAuthorities()
        );
        //authenticate with Java Spring Security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
}
