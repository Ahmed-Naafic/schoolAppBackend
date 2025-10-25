package com.SchoolManagementSystem.School_Management_System.repository;

import com.SchoolManagementSystem.School_Management_System.model.Attendance;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);

    List<Attendance> findBySubject(Subject subject);

    List<Attendance> findByStudentAndSubject(Student student, Subject subject);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentAndDate(Student student, LocalDate date);
}
