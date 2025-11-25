package com.SchoolManagementSystem.School_Management_System.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Subject IDs are required")
    private List<Long> subjectIds;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    private String discountType = "percentage"; // "percentage" or "fixed"

    private Double discountValue = 0.0;

    private Double amountPaid = 0.0;

    private LocalDate paymentDate;

    private String academicYear;

    private String term;
}




