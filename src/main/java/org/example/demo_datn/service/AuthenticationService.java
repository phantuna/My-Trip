package org.example.demo_datn.service;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Request.AuthenticationRequest;
import org.example.demo_datn.Dto.Response.AuthenticationResponse;
import org.example.demo_datn.Entity.Permission;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.StatusUser;
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
//    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {


        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == StatusUser.BLOCKED) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        List<String> permissions = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermission)
                .distinct()
                .toList();


        String token = jwtService.generateToken(user.getUsername(), permissions);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .permissions(permissions)
                .build();
    }
}