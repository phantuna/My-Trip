package org.example.demo_datn.service.auth;

import lombok.AllArgsConstructor;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;



}
