package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.FeeRequest;
import com.SchoolManagementSystem.School_Management_System.dto.FeeUpdateRequest;
import com.SchoolManagementSystem.School_Management_System.model.Fee;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.repository.FeeRepository;
import com.SchoolManagementSystem.School_Management_System.repository.StudentRepository;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import com.SchoolManagementSystem.School_Management_System.util.FeeCalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByCategory(String category) {
        List<Fee> allFees = feeRepository.findAll();
        return allFees.stream()
                .filter(fee -> category.equalsIgnoreCase(fee.getCategory()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByStudent(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesBySubject(Long subjectId) {
        return feeRepository.findBySubjectId(subjectId);
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByStatus(String status) {
        return feeRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByClass(String className) {
        return feeRepository.findAll().stream()
                .filter(fee -> fee.getStudent() != null && 
                        (className.equalsIgnoreCase(fee.getStudent().getClassName()) ||
                         className.equalsIgnoreCase(fee.getStudent().getClass())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByAcademicYear(String academicYear) {
        return feeRepository.findAll().stream()
                .filter(fee -> academicYear.equals(fee.getAcademicYear()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Fee> getFeesByTerm(String term) {
        return feeRepository.findAll().stream()
                .filter(fee -> term.equals(fee.getTerm()))
                .collect(Collectors.toList());
    }

    /**
     * Calculate and set all fee-related fields using utility functions
     */
    private void calculateAndSetFeeFields(Fee fee, Double totalAmount, String discountType, Double discountValue) {
        // Calculate discount amount
        Double discountAmount = FeeCalculationUtil.calculateDiscountAmount(totalAmount, discountType, discountValue);
        fee.setDiscountAmount(discountAmount);

        // Calculate amount due
        Double amountDue = FeeCalculationUtil.calculateAmountDue(totalAmount, discountAmount);
        fee.setAmountDue(amountDue != null ? amountDue : 0.0);

        // Calculate balance (amountDue - amountPaid)
        Double balance = FeeCalculationUtil.calculateBalance(amountDue, fee.getAmountPaid());
        fee.setBalance(balance != null ? balance : 0.0);

        // Set final amount (same as amountDue for consistency) - ensure it's never null
        fee.setFinalAmount(amountDue != null ? amountDue : 0.0);

        // Determine category
        String category = FeeCalculationUtil.determineCategory(totalAmount, discountAmount);
        fee.setCategory(category);

        // Update status based on balance
        if (balance <= 0 && fee.getAmountPaid() > 0) {
            fee.setStatus("PAID");
        } else if (fee.getAmountPaid() > 0 && balance > 0) {
            fee.setStatus("PARTIAL");
        } else {
            fee.setStatus("PENDING");
        }
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        LocalDate today = LocalDate.now();
        Long count = feeRepository.count();
        Long sequenceNumber = (count != null ? count : 0L) + 1;
        return FeeCalculationUtil.generateInvoiceNumber(sequenceNumber);
    }

    @Transactional
    public List<Fee> createFees(FeeRequest feeRequest) {
        // Validate discount
        if (!FeeCalculationUtil.validateDiscount(feeRequest.getDiscountValue(), feeRequest.getDiscountType())) {
            throw new RuntimeException("Invalid discount value. Percentage must be between 0-100.");
        }

        Student student = studentRepository.findById(feeRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + feeRequest.getStudentId()));

        List<Fee> fees = new ArrayList<>();
        
        // Calculate per-subject amount if multiple subjects
        Double totalAmount = feeRequest.getTotalAmount();
        int subjectCount = feeRequest.getSubjectIds().size();
        Double perSubjectAmount = subjectCount > 0 ? totalAmount / subjectCount : totalAmount;

        // Create a fee record for each subject
        for (Long subjectId : feeRequest.getSubjectIds()) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

            Fee fee = new Fee();
            fee.setStudent(student);
            fee.setSubject(subject);
            fee.setTotalAmount(perSubjectAmount);
            fee.setDiscountType(feeRequest.getDiscountType() != null ? feeRequest.getDiscountType() : "percentage");
            fee.setDiscountValue(feeRequest.getDiscountValue() != null ? feeRequest.getDiscountValue() : 0.0);
            fee.setAmountPaid(feeRequest.getAmountPaid() != null ? feeRequest.getAmountPaid() : 0.0);
            fee.setPaymentDate(feeRequest.getPaymentDate() != null ? feeRequest.getPaymentDate() : LocalDate.now());
            fee.setAcademicYear(feeRequest.getAcademicYear());
            fee.setTerm(feeRequest.getTerm());
            fee.setInvoiceNumber(generateInvoiceNumber());
            
            // Initialize finalAmount to 0.0 to ensure it's never null before calculation
            fee.setFinalAmount(0.0);

            // Calculate all fields (this will update finalAmount)
            calculateAndSetFeeFields(fee, perSubjectAmount, fee.getDiscountType(), fee.getDiscountValue());

            fees.add(feeRepository.save(fee));
        }

        return fees;
    }

    @Transactional
    public Fee createSingleFee(FeeRequest feeRequest) {
        // Validate discount
        if (!FeeCalculationUtil.validateDiscount(feeRequest.getDiscountValue(), feeRequest.getDiscountType())) {
            throw new RuntimeException("Invalid discount value. Percentage must be between 0-100.");
        }

        Student student = studentRepository.findById(feeRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + feeRequest.getStudentId()));

        if (feeRequest.getSubjectIds() == null || feeRequest.getSubjectIds().isEmpty()) {
            throw new RuntimeException("At least one subject ID is required");
        }

        Subject subject = subjectRepository.findById(feeRequest.getSubjectIds().get(0))
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + feeRequest.getSubjectIds().get(0)));

        Fee fee = new Fee();
        fee.setStudent(student);
        fee.setSubject(subject);
        fee.setTotalAmount(feeRequest.getTotalAmount());
        fee.setDiscountType(feeRequest.getDiscountType() != null ? feeRequest.getDiscountType() : "percentage");
        fee.setDiscountValue(feeRequest.getDiscountValue() != null ? feeRequest.getDiscountValue() : 0.0);
        fee.setAmountPaid(feeRequest.getAmountPaid() != null ? feeRequest.getAmountPaid() : 0.0);
        fee.setPaymentDate(feeRequest.getPaymentDate() != null ? feeRequest.getPaymentDate() : LocalDate.now());
        fee.setAcademicYear(feeRequest.getAcademicYear());
        fee.setTerm(feeRequest.getTerm());
        fee.setInvoiceNumber(generateInvoiceNumber());
        
        // Initialize finalAmount to 0.0 to ensure it's never null before calculation
        fee.setFinalAmount(0.0);

        // Calculate all fields (this will update finalAmount)
        calculateAndSetFeeFields(fee, feeRequest.getTotalAmount(), fee.getDiscountType(), fee.getDiscountValue());

        return feeRepository.save(fee);
    }

    @Transactional(readOnly = true)
    public Fee getFeeById(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
    }

    @Transactional
    public Fee updateFee(Long id, FeeUpdateRequest updateRequest) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));

        // Update discount if provided
        if (updateRequest.getDiscountType() != null) {
            fee.setDiscountType(updateRequest.getDiscountType());
        }
        if (updateRequest.getDiscountValue() != null) {
            // Validate discount
            if (!FeeCalculationUtil.validateDiscount(updateRequest.getDiscountValue(), fee.getDiscountType())) {
                throw new RuntimeException("Invalid discount value. Percentage must be between 0-100.");
            }
            fee.setDiscountValue(updateRequest.getDiscountValue());
        }

        // Update amount paid if provided
        if (updateRequest.getAmountPaid() != null) {
            if (updateRequest.getAmountPaid() < 0) {
                throw new RuntimeException("Amount paid cannot be negative");
            }
            fee.setAmountPaid(updateRequest.getAmountPaid());
        }

        // Update status if provided
        if (updateRequest.getStatus() != null) {
            fee.setStatus(updateRequest.getStatus());
        }

        // Recalculate all fields (this will also recalculate status based on balance if status wasn't manually set)
        calculateAndSetFeeFields(fee, fee.getTotalAmount(), fee.getDiscountType(), fee.getDiscountValue());
        
        // If status was manually set, override the calculated status
        if (updateRequest.getStatus() != null) {
            fee.setStatus(updateRequest.getStatus());
        }

        // Update payment date if provided
        if (updateRequest.getPaymentDate() != null) {
            fee.setPaymentDate(updateRequest.getPaymentDate());
        }

        // Update academic year and term if provided
        if (updateRequest.getAcademicYear() != null) {
            fee.setAcademicYear(updateRequest.getAcademicYear());
        }
        if (updateRequest.getTerm() != null) {
            fee.setTerm(updateRequest.getTerm());
        }

        return feeRepository.save(fee);
    }

    @Transactional
    public Fee addPayment(Long id, Double paymentAmount) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));

        if (paymentAmount == null || paymentAmount <= 0) {
            throw new RuntimeException("Payment amount must be greater than 0");
        }

        // Add to existing amount paid
        Double newAmountPaid = (fee.getAmountPaid() != null ? fee.getAmountPaid() : 0.0) + paymentAmount;
        
        // Ensure amount paid doesn't exceed amount due
        if (newAmountPaid > fee.getAmountDue()) {
            throw new RuntimeException("Payment amount exceeds amount due. Maximum payment: $" + fee.getAmountDue());
        }

        fee.setAmountPaid(newAmountPaid);
        
        // Recalculate balance
        Double balance = FeeCalculationUtil.calculateBalance(fee.getAmountDue(), newAmountPaid);
        fee.setBalance(balance);

        // Update status
        if (balance <= 0) {
            fee.setStatus("PAID");
        } else {
            fee.setStatus("PARTIAL");
        }

        return feeRepository.save(fee);
    }

    @Transactional
    public void deleteFee(Long id) {
        if (!feeRepository.existsById(id)) {
            throw new RuntimeException("Fee not found with id: " + id);
        }
        feeRepository.deleteById(id);
    }

    /**
     * Get fee summary statistics
     */
    @Transactional(readOnly = true)
    public FeeSummaryStatistics getFeeSummaryStatistics() {
        List<Fee> allFees = feeRepository.findAll();
        
        FeeSummaryStatistics stats = new FeeSummaryStatistics();
        
        for (Fee fee : allFees) {
            String category = fee.getCategory();
            
            if ("FREE".equals(category)) {
                stats.freeStudentCount++;
                stats.totalWaivedAmount += (fee.getTotalAmount() != null ? fee.getTotalAmount() : 0.0);
            } else if ("DISCOUNT".equals(category)) {
                stats.discountStudentCount++;
                stats.totalDiscountAmount += (fee.getDiscountAmount() != null ? fee.getDiscountAmount() : 0.0);
                stats.totalDiscountDue += (fee.getAmountDue() != null ? fee.getAmountDue() : 0.0);
            } else if ("FULL_PAYMENT".equals(category)) {
                stats.fullPayStudentCount++;
                stats.totalFullPayDue += (fee.getAmountDue() != null ? fee.getAmountDue() : 0.0);
            }
        }
        
        return stats;
    }

    /**
     * Inner class for fee summary statistics
     */
    public static class FeeSummaryStatistics {
        public int freeStudentCount = 0;
        public double totalWaivedAmount = 0.0;
        public int discountStudentCount = 0;
        public double totalDiscountAmount = 0.0;
        public double totalDiscountDue = 0.0;
        public int fullPayStudentCount = 0;
        public double totalFullPayDue = 0.0;
    }
}
