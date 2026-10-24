package com.vhub.smart.placement.service;

import com.vhub.smart.placement.dto.LoginRequest;
import com.vhub.smart.placement.dto.RegisterRequest;
import com.vhub.smart.placement.entity.User;
import com.vhub.smart.placement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public Map<String, Object> registerUser(RegisterRequest request) {
        if (request.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new IllegalArgumentException("Password must be at most 72 UTF-8 bytes");
        if (userRepository.findByEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT)).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("STUDENT");

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return response;
    }

    public Map<String, Object> loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase(java.util.Locale.ROOT))
                .orElseThrow(() -> new RuntimeException("Invalid credentials!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return response;
    }
}