package com.SchoolManagementSystem.School_Management_System.repository;

import com.SchoolManagementSystem.School_Management_System.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT DISTINCT s FROM Subject s LEFT JOIN FETCH s.teacher")
    List<Subject> findAllWithTeacher();

    @Query("SELECT s FROM Subject s LEFT JOIN FETCH s.teacher WHERE s.id = :id")
    Optional<Subject> findByIdWithTeacher(Long id);
}
