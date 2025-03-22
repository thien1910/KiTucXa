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

//            if (userRepository.findByuserName("GUEST").isEmpty()){
//                var guestRoles = new HashSet<String>();
//                guestRoles.add(Role.GUEST.name());
//
//                User guest = User.builder()
//                        .userName("GUEST")
//                        .passWord(passwordEncoder.encode("GUEST"))
//                        .roles(guestRoles)
//                        .build();
//
//                userRepository.save(guest);
//                log.warn("Guest user has been created with default password: GUEST, please change it");
//            }
        };
    }
}
