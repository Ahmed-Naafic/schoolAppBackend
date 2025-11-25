package com.SchoolManagementSystem.School_Management_System.repository;

import com.SchoolManagementSystem.School_Management_System.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
    
    List<Student> findByClassName(String className);
}
