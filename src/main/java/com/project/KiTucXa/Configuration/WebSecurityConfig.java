package com.project.KiTucXa.Configuration;

import com.project.KiTucXa.Filters.JwtTokenFilter;
import com.project.KiTucXa.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    // all quyen
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/user/login",
            "/api/v1/user/add",
            "/api/v1/user/guest-login",
            "/api/v1/user/myInfo",
            "/api/v1/user/guest-login",

    };
    // quyen của ADMINISTRATOR
    private final String[] ADMINISTRATOR_ENDPOINTS = {
            "/api/v1/user/add",
            "/api/v1/user/list",
            "/api/v1/user/{userId}",
            "/api/v1/user/update/{userId}",
            "/api/v1/user/delete/{userId}"

    };
    // QUYEN CUA GUEST
    private final String[] GUEST_ENDPOINTS ={
            "/api/v1/rooms/list"
    };
    // quyền của student
    private final String[] STUDENT_ENDPOINTS = {
            "/api/v1/user/{userId}",
            "/api/v1/user/update/{userId}",
            "/api/v1/bills/{billId}",
            "/api/v1/bill-details/{billDetailId}",
            "/api/v1/Payment/{paymentId}",
            "/api/v1/rooms/list","/api/rooms/{roomId}",
            "/api/v1/room-services/{roomServiceId}",
            "/api/v1/contracts/{contractId}"
    };
    // QUYEN CUA DUTY_STAFF
    private final String[] DUTY_STAFF_ENDPOINTS = {
            "/api/v1/user/{userId}",
            "/api/v1/user/update/{userId}",
            "/api/v1/bills/{billId}",
            "/api/v1/bill-details/{billDetailId}",
            "/api/v1/Payment/{paymentId}",
            "/api/v1/rooms/list","/api/rooms/{roomId}",
            "/api/v1/room-services/{roomServiceId}",
            "/api/v1/contracts/{contractId}"
    };
    // QUYEN CUA MANAGER
    private final String[] MANAGER_ENDPOINTS = {
            "/api/v1/user/{userId}","/api/v1/user/list","/api/v1/user/update/{userId}",
            "/api/v1/bills/{billId}","/api/v1/bills/list","/api/v1/bills/add","/api/v1/bills/update/{billId}","/api/v1/bills/delete/{billId}",
            "/api/v1/bill-details/{billDetailId}","/api/v1/bill-details/list","/api/v1/bill-details/add","/api/v1/bill-details/update/{billDetailId}",
            "/api/v1/Payment/add","/api/v1/Payment/list","/api/v1/Payment/{paymentId}","/api/v1/Payment/update/{paymentId}",
            "/api/v1/rooms/list","/api/v1/rooms/{roomId}","/api/v1/rooms/add","/api/v1/rooms/update/{roomId}","/api/v1/rooms/delete/{roomId}",
            "/api/v1/room-services/add", "/api/v1/room-services/{roomServiceId}", "/api/v1/room-services/list", "/api/v1/room-services/update/{roomServiceId}", "/api/v1/room-services/delete/{roomServiceId}",
            "/api/v1/contracts/{contractId}","/api/v1/contracts/add","/api/v1/contracts/list","/api/v1/contracts/update/{contractId}","/api/v1/contracts/delete/{contractId}",
            "/api/v1/utility-services/add","/api/v1/utility-services/{utilityServiceId}","/api/v1/utility-services/list","/api/v1/utility-services/update/{utilityServiceId}","/api/v1/utility-services/delete/{utilityServiceId}",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                            .requestMatchers(ADMINISTRATOR_ENDPOINTS).permitAll()
                            .requestMatchers(GUEST_ENDPOINTS).permitAll()
                            .requestMatchers(STUDENT_ENDPOINTS).permitAll()
                            .requestMatchers(MANAGER_ENDPOINTS).permitAll()
                            .anyRequest().authenticated();

                })
        ;
        return http.build();
    }
}
/*public class WebSecurityConfig {
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

                            *//*.requestMatchers(GET,
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
*//*
                            .anyRequest().authenticated();

                });
        return http.build();
    }
}*/

