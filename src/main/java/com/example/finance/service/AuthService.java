package com.example.finance.service;

import com.example.finance.dto.AuthDtos.RegisterRequest;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User(
                request.username().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                request.phoneNumber());
        return userRepository.save(user);
    }
}
