package com.SchoolManagementSystem.School_Management_System.controller;

import com.SchoolManagementSystem.School_Management_System.dto.BulkExamRequest;
import com.SchoolManagementSystem.School_Management_System.dto.ExamRequest;
import com.SchoolManagementSystem.School_Management_System.model.Exam;
import com.SchoolManagementSystem.School_Management_System.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        List<Exam> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        Optional<Exam> exam = examService.getExamById(id);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Exam>> getExamsByStudent(@PathVariable Long studentId) {
        List<Exam> exams = examService.getExamsByStudent(studentId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Exam>> getExamsBySubject(@PathVariable Long subjectId) {
        List<Exam> exams = examService.getExamsBySubject(subjectId);
        return ResponseEntity.ok(exams);
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody ExamRequest examRequest) {
        Exam exam = examService.createExam(examRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(exam);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Exam>> createBulkExams(@Valid @RequestBody BulkExamRequest bulkExamRequest) {
        List<Exam> exams = examService.createBulkExams(bulkExamRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(exams);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest examRequest) {
        Exam exam = examService.updateExam(id, examRequest);
        return ResponseEntity.ok(exam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
