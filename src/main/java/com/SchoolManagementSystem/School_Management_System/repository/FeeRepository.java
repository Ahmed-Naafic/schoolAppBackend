package com.SchoolManagementSystem.School_Management_System.repository;

import com.SchoolManagementSystem.School_Management_System.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudentId(Long studentId);
    List<Fee> findBySubjectId(Long subjectId);
    
    @Query("SELECT f FROM Fee f WHERE YEAR(f.paymentDate) = :year AND MONTH(f.paymentDate) = :month")
    List<Fee> findByPaymentDateYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    List<Fee> findByStatus(String status);
    
    void deleteByStudentId(Long studentId);
}
