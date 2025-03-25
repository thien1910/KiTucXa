package com.project.KiTucXa.Service;

import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Status;
import com.project.KiTucXa.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByuserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Chặn đăng nhập nếu user bị Disciplined
        switch (user.getStatus()) {
            case Left, Disciplined -> throw new DisabledException("Your account is " + user.getStatus() + " and cannot log in.");
            case Staying -> {} // Cho phép đăng nhập
        }


        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassWord(),
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

}
