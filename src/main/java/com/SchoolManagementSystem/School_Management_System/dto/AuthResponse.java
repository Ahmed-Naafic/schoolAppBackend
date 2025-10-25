package com.SchoolManagementSystem.School_Management_System.dto;

import com.SchoolManagementSystem.School_Management_System.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Role role;
    private String message;
}
