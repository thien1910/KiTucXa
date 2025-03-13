package com.project.KiTucXa.Configuration;

import com.project.KiTucXa.Filters.JwtTokenFilter;
import com.project.KiTucXa.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;


@Configuration
//@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    //Pair.of(String.format("%s/products", apiPrefix), "GET"),
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/user/add", apiPrefix),
                                    String.format("%s/user/login", apiPrefix),
                                    String.format("%s/user/login",apiPrefix),
                                    String.format("%s/user/myInfo",apiPrefix),
                                    String.format("%s/user/guest-login",apiPrefix)
                            )
                            .permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/user/list", apiPrefix)).hasAnyRole(Role.ADMINISTRATOR,Role.MANAGER,Role.DUTY_STAFF)

                            .requestMatchers(GET,
                                    String.format("%s/user/{userId}", apiPrefix)).hasAnyRole(Role.ADMINISTRATOR,Role.MANAGER,Role.DUTY_STAFF,Role.STUDENT)

                            .requestMatchers(PUT,
                                    String.format("%s/user/update/{userId}", apiPrefix)).hasAnyRole(Role.ADMINISTRATOR,Role.MANAGER,Role.DUTY_STAFF,Role.STUDENT)

                            .requestMatchers(DELETE,
                                    String.format("%s/user/update/delete/{userId}", apiPrefix)).hasRole(Role.ADMINISTRATOR)

                            .requestMatchers(POST,
                                    String.format("%s/bills/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/bills/update/{billId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/bills/list", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/bills/{billId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF)

                            .requestMatchers(GET,
                                    String.format("%s/bill-details/list", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(POST,
                                    String.format("%s/bill-details/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/bill-details/{billDetailId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF)

                            .requestMatchers(POST,
                                    String.format("%s/Payment/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/Payment/update/{paymentId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/Payment/list", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/Payment/{paymentId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF)

                            .requestMatchers(POST,
                                    String.format("%s/rooms/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/rooms/update/{roomId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(DELETE,
                                    String.format("%s/rooms/delete/{roomId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/rooms/list", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF,Role.GUEST)

                            .requestMatchers(GET,
                                    String.format("%s/rooms/{roomId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF,Role.GUEST)

                            .requestMatchers(POST,
                                    String.format("%s/room-services/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/room-services/update/{roomServiceId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(DELETE,
                                    String.format("%s/room-services/delete/{roomServiceId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/room-services/list", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/room-services/{roomServiceId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF)

                            .requestMatchers(POST,
                                    String.format("%s/contracts/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/contracts/update/{contractId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(DELETE,
                                    String.format("%s/contracts/delete/{contractId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/contracts/list", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/contracts/update/{contractId}", apiPrefix)).hasAnyRole(Role.MANAGER,Role.STUDENT, Role.DUTY_STAFF)

                            .requestMatchers(POST,
                                    String.format("%s/utility-services/add", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(PUT,
                                    String.format("%s/utility-services/update/{utilityServiceId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(DELETE,
                                    String.format("%s/utility-services/delete/{utilityServiceId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/utility-services/{utilityServiceId}", apiPrefix)).hasRole(Role.MANAGER)

                            .requestMatchers(GET,
                                    String.format("%s/utility-services/list", apiPrefix)).hasRole(Role.MANAGER)

                            .anyRequest().authenticated();

                });
        return http.build();
    }
}
