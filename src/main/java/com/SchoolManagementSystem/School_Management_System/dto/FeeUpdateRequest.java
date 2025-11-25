package com.SchoolManagementSystem.School_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeUpdateRequest {
    private String discountType; // "percentage" or "fixed"
    private Double discountValue;
    private Double amountPaid;
    private String status; // PAID, PENDING, CANCELLED, PARTIAL
    private LocalDate paymentDate;
    private String academicYear;
    private String term;
}
