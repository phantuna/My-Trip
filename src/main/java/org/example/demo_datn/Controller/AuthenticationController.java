package org.example.demo_datn.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Request.AuthenticationRequest;
import org.example.demo_datn.Dto.Response.ApiResponse;
import org.example.demo_datn.Dto.Response.AuthenticationResponse;
import org.example.demo_datn.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {

        ApiResponse< AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(authenticationService.authenticate(request));
        return response;
    }
}
