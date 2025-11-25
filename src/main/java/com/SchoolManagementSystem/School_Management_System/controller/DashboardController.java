package com.SchoolManagementSystem.School_Management_System.controller;

import com.SchoolManagementSystem.School_Management_System.model.Attendance;
import com.SchoolManagementSystem.School_Management_System.model.Exam;
import com.SchoolManagementSystem.School_Management_System.model.Fee;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.repository.AttendanceRepository;
import com.SchoolManagementSystem.School_Management_System.repository.ExamRepository;
import com.SchoolManagementSystem.School_Management_System.repository.FeeRepository;
import com.SchoolManagementSystem.School_Management_System.repository.StudentRepository;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import com.SchoolManagementSystem.School_Management_System.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeeRepository feeRepository;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatistics> getStatistics() {
        DashboardStatistics stats = new DashboardStatistics();
        
        // Total counts
        stats.setTotalStudents(studentRepository.count());
        stats.setTotalTeachers(teacherRepository.count());
        stats.setTotalSubjects(subjectRepository.count());
        stats.setTotalExams(examRepository.count());
        stats.setTotalAttendance(attendanceRepository.count());
        
        // Calculate fees collected
        List<Fee> allFees = feeRepository.findAll();
        double totalFeesCollected = allFees.stream()
                .mapToDouble(Fee::getFinalAmount)
                .sum();
        stats.setTotalFeesCollected(totalFeesCollected);
        
        // Calculate pass/fail ratio
        List<Exam> allExams = examRepository.findAll();
        long passedExams = allExams.stream()
                .filter(exam -> exam.getObtainedMarks() != null && exam.getTotalMarks() != null)
                .filter(exam -> {
                    double percentage = (double) exam.getObtainedMarks() / exam.getTotalMarks() * 100;
                    return percentage >= 50;
                })
                .count();
        long failedExams = allExams.size() - passedExams;
        stats.setPassedExams(passedExams);
        stats.setFailedExams(failedExams);
        
        // Calculate attendance rate
        List<Attendance> allAttendance = attendanceRepository.findAll();
        long presentCount = allAttendance.stream()
                .filter(att -> att.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
        long absentCount = allAttendance.size() - presentCount;
        double attendanceRate = allAttendance.size() > 0 
                ? (double) presentCount / allAttendance.size() * 100 
                : 0;
        stats.setPresentCount(presentCount);
        stats.setAbsentCount(absentCount);
        stats.setAttendanceRate(attendanceRate);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/charts/exam-performance")
    public ResponseEntity<List<Map<String, Object>>> getExamPerformanceChart() {
        List<Exam> exams = examRepository.findAll();
        Map<String, Map<String, Long>> performanceByMonth = new HashMap<>();
        
        for (Exam exam : exams) {
            if (exam.getExamDate() == null) continue;
            
            String monthKey = exam.getExamDate().toString().substring(0, 7); // YYYY-MM
            
            performanceByMonth.putIfAbsent(monthKey, new HashMap<>());
            Map<String, Long> monthData = performanceByMonth.get(monthKey);
            
            if (exam.getObtainedMarks() != null && exam.getTotalMarks() != null) {
                double percentage = (double) exam.getObtainedMarks() / exam.getTotalMarks() * 100;
                if (percentage >= 50) {
                    monthData.put("passed", monthData.getOrDefault("passed", 0L) + 1);
                } else {
                    monthData.put("failed", monthData.getOrDefault("failed", 0L) + 1);
                }
            }
        }
        
        List<Map<String, Object>> chartData = performanceByMonth.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("month", entry.getKey());
                    data.put("passed", entry.getValue().getOrDefault("passed", 0L));
                    data.put("failed", entry.getValue().getOrDefault("failed", 0L));
                    return data;
                })
                .sorted((a, b) -> a.get("month").toString().compareTo(b.get("month").toString()))
                .toList();
        
        return ResponseEntity.ok(chartData);
    }

    @Data
    @AllArgsConstructor
    public static class DashboardStatistics {
        private long totalStudents;
        private long totalTeachers;
        private long totalSubjects;
        private long totalExams;
        private long totalAttendance;
        private double totalFeesCollected;
        private long passedExams;
        private long failedExams;
        private long presentCount;
        private long absentCount;
        private double attendanceRate;
        
        public DashboardStatistics() {
            // Default constructor
        }
    }
}




