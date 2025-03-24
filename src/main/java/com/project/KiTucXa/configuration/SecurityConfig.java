package com.project.KiTucXa.configuration;

import com.project.KiTucXa.Enum.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/user/guest-login",
            "/api/v1/auth/token",
            "/api/v1/auth/introspect",
            "/api/v1/rooms/list",
            "/api/v1/user/{userId}",
    };
    private final String[] MANAGER_ENDPOINTS = {
            "/api/v1/bills/{billId}","/api/v1/bills/list","/api/v1/bills/add","/api/v1/bills/update/{billId}","/api/v1/bills/delete/{billId}",
            "/api/v1/user/manager/list",

            "/api/v1/bill-details/{billDetailId}","/api/v1/bill-details/list","/api/v1/bill-details/add","/api/v1/bill-details/update/{billDetailId}",
            "/api/v1/Payment/add","/api/v1/Payment/list","/api/v1/Payment/{paymentId}","/api/v1/Payment/update/{paymentId}",
            "/api/v1/rooms/list","/api/v1/rooms/{roomId}","/api/v1/rooms/add","/api/v1/rooms/update/{roomId}","/api/v1/rooms/delete/{roomId}",
            "/api/v1/room-services/add", "/api/v1/room-services/{roomServiceId}", "/api/v1/room-services/list", "/api/v1/room-services/update/{roomServiceId}", "/api/v1/room-services/delete/{roomServiceId}",
            "/api/v1/contracts/{contractId}","/api/v1/contracts/add","/api/v1/contracts/list","/api/v1/contracts/update/{contractId}","/api/v1/contracts/delete/{contractId}",
            "/api/v1/utility-services/add","/api/v1/utility-services/{utilityServiceId}","/api/v1/utility-services/list","/api/v1/utility-services/update/{utilityServiceId}","/api/v1/utility-services/delete/{utilityServiceId}",
    };
    // quyền của student
    private final String[] STUDENT_ENDPOINTS = {
           "/api/v1/student/update/{studentId}",
            "/api/v1/student/{studentId}",
            "/api/v1/bills/{billId}",
            "/api/v1/bill-details/{billDetailId}",
            "/api/v1/Payment/{paymentId}",
            "/api/v1/rooms/list","/api/rooms/{roomId}",
            "/api/v1/room-services/{roomServiceId}",
            "/api/v1/contracts/{contractId}",
            "/api/v1/user/my-info",
            "/api/v1/Payment/add",
            "/api/v1/Payment/{paymentId}"

    };
    private final String[] ADMIN_ENDPOINTS = {
            "/api/v1/user/add",
            "/api/v1/user/list",
            "/api/v1/user/update/{userId}",

    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()

                        .requestMatchers(HttpMethod.POST, MANAGER_ENDPOINTS)
                        .hasRole(Role.MANAGER.name())
                        .requestMatchers(HttpMethod.GET, MANAGER_ENDPOINTS)
                        .hasRole(Role.MANAGER.name())
                        .requestMatchers(HttpMethod.PUT, MANAGER_ENDPOINTS)
                        .hasRole(Role.MANAGER.name())
                        .requestMatchers(HttpMethod.DELETE, MANAGER_ENDPOINTS)
                        .hasRole(Role.MANAGER.name())

                        .requestMatchers(HttpMethod.PUT, STUDENT_ENDPOINTS)
                        .hasRole(Role.STUDENT.name())
                        .requestMatchers(HttpMethod.GET, STUDENT_ENDPOINTS)
                        .hasRole(Role.STUDENT.name())

                        .requestMatchers(HttpMethod.POST, ADMIN_ENDPOINTS)
                        .hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, ADMIN_ENDPOINTS)
                        .hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, ADMIN_ENDPOINTS)
                        .hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, ADMIN_ENDPOINTS)
                        .hasRole(Role.ADMIN.name())

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Domain của frontend
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfiguration.setAllowCredentials(true); // Bật nếu gửi cookie/token
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
