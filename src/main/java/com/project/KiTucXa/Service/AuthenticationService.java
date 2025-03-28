package com.project.KiTucXa.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.KiTucXa.Dto.Request.AuthenticationRequest;
import com.project.KiTucXa.Dto.Request.IntrospectRequest;
import com.project.KiTucXa.Dto.Response.AuthenticationResponse;
import com.project.KiTucXa.Dto.Response.IntrospectResponse;
import com.project.KiTucXa.Entity.User;
import com.project.KiTucXa.Enum.Status;
import com.project.KiTucXa.Exception.AppException;
import com.project.KiTucXa.Exception.ErrorCode;
import com.project.KiTucXa.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    public String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            var token = request.getToken();
            log.info("Received token for introspection: {}", token);

            // Kiểm tra định dạng token trước khi parse
            if (token == null || !token.matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$")) {
                log.error("Invalid token format");
                return IntrospectResponse.builder().valid(false).build();
            }

            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean verified = signedJWT.verify(verifier);
            boolean isExpired = signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date());

            return IntrospectResponse.builder().valid(verified && !isExpired).build();
        } catch (ParseException e) {
            log.error("Error parsing token", e);
        } catch (JOSEException e) {
            log.error("Token verification failed", e);
        }

        return IntrospectResponse.builder().valid(false).build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByuserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));

        // Kiểm tra nếu user có trạng thái DISCIPLINED thì không cho đăng nhập
        if (user.getStatus() == Status.Disciplined) {
            throw new AppException(ErrorCode.USER_FORBIDDENED);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassWord(), user.getPassWord());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .roles(user.getRoles()) // Thêm role của user vào response
                .fullName(user.getFullName()) // Thêm fullName vào response
                .userId(user.getUserId())
                .status(user.getStatus()) // Trả về status của user
                .build();
    }




    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(stringJoiner::add);

        return stringJoiner.toString();
    }
}

