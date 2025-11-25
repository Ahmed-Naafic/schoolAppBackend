package com.SchoolManagementSystem.School_Management_System.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"exams", "students", "attendances", "fees", "hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"exams", "subjects", "attendances", "fees", "hibernateLazyInitializer", "handler"})
    private Student student;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "obtained_marks")
    private Integer obtainedMarks;
}
