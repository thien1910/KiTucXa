package com.project.KiTucXa.configuration;

import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Role;
import com.project.KiTucXa.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static org.apache.coyote.http11.Constants.a;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if (userRepository.findByuserName("MANAGER").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Role.MANAGER.name());

                User user = User.builder()
                        .userName("MANAGER")
                        .passWord(passwordEncoder.encode("MANAGER"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("Manager user has been created with default password: Manager, please change it");
            }
            if (userRepository.findByuserName("ADMIN").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());

                User user = User.builder()
                        .userName("ADMIN")
                        .passWord(passwordEncoder.encode("ADMIN"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: Admin, please change it");
            }
        };
    }
}
