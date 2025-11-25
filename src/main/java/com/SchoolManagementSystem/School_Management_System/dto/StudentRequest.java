package com.SchoolManagementSystem.School_Management_System.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String phone;
    private String gender;
    private String address;
    private LocalDate dateOfBirth;
    
    @JsonProperty("class")
    private String className;
    private String parentName;
    private String parentPhone;
    private Double discount = 0.0;
    private List<Long> subjectIds;
}
