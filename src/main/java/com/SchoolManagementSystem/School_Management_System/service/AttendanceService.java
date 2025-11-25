package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.AttendanceRequest;
import com.SchoolManagementSystem.School_Management_System.dto.BulkAttendanceRequest;
import com.SchoolManagementSystem.School_Management_System.model.Attendance;
import com.SchoolManagementSystem.School_Management_System.model.Attendance.AttendanceStatus;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.repository.AttendanceRepository;
import com.SchoolManagementSystem.School_Management_System.repository.StudentRepository;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public Attendance createAttendance(AttendanceRequest attendanceRequest) {
        Student student = studentRepository.findById(attendanceRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + attendanceRequest.getStudentId()));

        Subject subject = subjectRepository.findById(attendanceRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + attendanceRequest.getSubjectId()));

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setDate(attendanceRequest.getDate());
        attendance.setStatus(attendanceRequest.getStatus());

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public List<Attendance> createBulkAttendance(BulkAttendanceRequest bulkAttendanceRequest) {
        Subject subject = subjectRepository.findById(bulkAttendanceRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + bulkAttendanceRequest.getSubjectId()));

        LocalDate date = LocalDate.parse(bulkAttendanceRequest.getDate());
        List<Attendance> attendances = new ArrayList<>();

        for (var studentAttendanceData : bulkAttendanceRequest.getStudentAttendances()) {
            Student student = studentRepository.findById(studentAttendanceData.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentAttendanceData.getStudentId()));

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(date);
            attendance.setStatus(AttendanceStatus.valueOf(studentAttendanceData.getStatus()));

            attendances.add(attendanceRepository.save(attendance));
        }

        return attendances;
    }

    public Attendance updateAttendance(Long id, AttendanceRequest attendanceRequest) {
        Attendance existingAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));

        Student student = studentRepository.findById(attendanceRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + attendanceRequest.getStudentId()));

        Subject subject = subjectRepository.findById(attendanceRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + attendanceRequest.getSubjectId()));

        existingAttendance.setStudent(student);
        existingAttendance.setSubject(subject);
        existingAttendance.setDate(attendanceRequest.getDate());
        existingAttendance.setStatus(attendanceRequest.getStatus());

        return attendanceRepository.save(existingAttendance);
    }

    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("Attendance not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    public List<Attendance> getAttendanceByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceBySubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        return attendanceRepository.findBySubject(subject);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}
