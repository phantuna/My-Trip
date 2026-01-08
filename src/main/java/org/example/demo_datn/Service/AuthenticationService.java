package org.example.demo_datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Request.AuthenticationRequest;
import org.example.demo_datn.Dto.Response.AuthenticationResponse;
import org.example.demo_datn.Entity.Permission;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User driver = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_ROLE_NOT_FOUND));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), driver.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        List<String> permissions = driver.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermission)
                .distinct()
                .toList();


        String token = jwtService.generateToken(driver.getUsername(), permissions);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .permissions(permissions)
                .build();
    }
}