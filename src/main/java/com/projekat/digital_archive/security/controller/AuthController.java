package com.projekat.digital_archive.security.controller;

import com.projekat.digital_archive.entity.User;
import com.projekat.digital_archive.repository.UserRepository;
import com.projekat.digital_archive.security.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request
    ) {

        String email = request.get("username");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                    .status(401)
                    .body("Email and password are required");
        }

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid credentials");
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", user.getRole().name(),
                        "email", user.getEmail()
                )
        );
    }
}