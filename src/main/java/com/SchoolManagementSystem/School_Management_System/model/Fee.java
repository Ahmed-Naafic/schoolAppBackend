package com.SchoolManagementSystem.School_Management_System.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "fees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"exams", "subjects", "attendances", "fees", "hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"exams", "students", "attendances", "fees", "hibernateLazyInitializer", "handler"})
    private Subject subject;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "discount_type")
    private String discountType; // "percentage" or "fixed"

    @Column(name = "discount_value")
    private Double discountValue = 0.0;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "amount_due", nullable = false)
    private Double amountDue;

    @Column(name = "amount_paid")
    private Double amountPaid = 0.0;

    @Column(name = "balance")
    private Double balance = 0.0;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;

    @Column(name = "category")
    private String category; // "FREE", "DISCOUNT", "FULL_PAYMENT"

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "status")
    private String status = "PENDING"; // PAID, PENDING, CANCELLED, PARTIAL

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "term")
    private String term;
}

