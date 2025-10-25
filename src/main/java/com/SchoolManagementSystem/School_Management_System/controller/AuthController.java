package com.SchoolManagementSystem.School_Management_System.controller;

import com.SchoolManagementSystem.School_Management_System.dto.AuthRequest;
import com.SchoolManagementSystem.School_Management_System.dto.AuthResponse;
import com.SchoolManagementSystem.School_Management_System.model.Role;
import com.SchoolManagementSystem.School_Management_System.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<AuthResponse> signupAdmin(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.signup(authRequest, Role.ADMIN);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/teacher")
    public ResponseEntity<AuthResponse> signupTeacher(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.signup(authRequest, Role.TEACHER);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/student")
    public ResponseEntity<AuthResponse> signupStudent(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.signup(authRequest, Role.STUDENT);
        return ResponseEntity.ok(response);
    }
}
