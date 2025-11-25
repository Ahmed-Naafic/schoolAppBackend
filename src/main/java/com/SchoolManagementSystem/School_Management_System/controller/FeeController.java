package com.SchoolManagementSystem.School_Management_System.controller;

import com.SchoolManagementSystem.School_Management_System.dto.FeeRequest;
import com.SchoolManagementSystem.School_Management_System.dto.FeeUpdateRequest;
import com.SchoolManagementSystem.School_Management_System.model.Fee;
import com.SchoolManagementSystem.School_Management_System.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getAllFees() {
        List<Fee> fees = feeService.getAllFees();
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/category/{category}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByCategory(@PathVariable String category) {
        List<Fee> fees = feeService.getFeesByCategory(category);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/student/{studentId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByStudent(@PathVariable Long studentId) {
        List<Fee> fees = feeService.getFeesByStudent(studentId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/subject/{subjectId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesBySubject(@PathVariable Long subjectId) {
        List<Fee> fees = feeService.getFeesBySubject(subjectId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/status/{status}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByStatus(@PathVariable String status) {
        List<Fee> fees = feeService.getFeesByStatus(status);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/class/{className}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByClass(@PathVariable String className) {
        List<Fee> fees = feeService.getFeesByClass(className);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/academic-year/{academicYear}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByAcademicYear(@PathVariable String academicYear) {
        List<Fee> fees = feeService.getFeesByAcademicYear(academicYear);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/term/{term}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fee>> getFeesByTerm(@PathVariable String term) {
        List<Fee> fees = feeService.getFeesByTerm(term);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/summary")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getFeeSummary() {
        FeeService.FeeSummaryStatistics stats = feeService.getFeeSummaryStatistics();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("freeStudents", Map.of(
            "count", stats.freeStudentCount,
            "totalWaivedAmount", stats.totalWaivedAmount
        ));
        summary.put("discountStudents", Map.of(
            "count", stats.discountStudentCount,
            "totalDiscountAmount", stats.totalDiscountAmount,
            "totalDue", stats.totalDiscountDue
        ));
        summary.put("fullPayStudents", Map.of(
            "count", stats.fullPayStudentCount,
            "totalDue", stats.totalFullPayDue
        ));
        
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createFee(@Valid @RequestBody FeeRequest feeRequest) {
        try {
            // Validate required fields manually for better error messages
            if (feeRequest.getStudentId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Student ID is required"));
            }
            if (feeRequest.getSubjectIds() == null || feeRequest.getSubjectIds().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "At least one subject ID is required"));
            }
            if (feeRequest.getTotalAmount() == null || feeRequest.getTotalAmount() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Total amount must be greater than 0"));
            }
            
            // If multiple subjects, create multiple fee records
            if (feeRequest.getSubjectIds().size() > 1) {
                List<Fee> fees = feeService.createFees(feeRequest);
                return ResponseEntity.status(HttpStatus.CREATED).body(fees);
            } else {
                // Single fee record
                Fee fee = feeService.createSingleFee(feeRequest);
                return ResponseEntity.status(HttpStatus.CREATED).body(fee);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Fee> getFeeById(@PathVariable Long id) {
        Fee fee = feeService.getFeeById(id);
        return ResponseEntity.ok(fee);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateFee(@PathVariable Long id, @RequestBody FeeUpdateRequest updateRequest) {
        try {
            Fee fee = feeService.updateFee(id, updateRequest);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Validation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/payment")
    @Transactional
    public ResponseEntity<?> addPayment(@PathVariable Long id, @RequestBody Map<String, Double> paymentRequest) {
        try {
            Double paymentAmount = paymentRequest.get("amount");
            if (paymentAmount == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Payment amount is required"));
            }
            Fee fee = feeService.addPayment(id, paymentAmount);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateFeeStatus(@PathVariable Long id, @RequestBody FeeUpdateRequest updateRequest) {
        try {
            Fee fee = feeService.updateFee(id, updateRequest);
            return ResponseEntity.ok(fee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
        return ResponseEntity.noContent().build();
    }
}
