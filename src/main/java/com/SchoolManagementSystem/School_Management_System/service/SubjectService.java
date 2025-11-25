package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.SubjectRequest;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.model.Teacher;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import com.SchoolManagementSystem.School_Management_System.repository.TeacherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return entityManager.createQuery(
                "SELECT DISTINCT s FROM Subject s LEFT JOIN FETCH s.teacher", 
                Subject.class
        ).getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<Subject> getSubjectById(Long id) {
        Subject subject = entityManager.createQuery(
                "SELECT s FROM Subject s LEFT JOIN FETCH s.teacher WHERE s.id = :id", 
                Subject.class
        ).setParameter("id", id)
        .getResultStream()
        .findFirst()
        .orElse(null);
        return Optional.ofNullable(subject);
    }

    @Transactional
    public Subject createSubject(SubjectRequest subjectRequest) {
        if (subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Subject with code " + subjectRequest.getCode() + " already exists");
        }

        // Create new Subject entity manually to avoid ModelMapper issues
        Subject subject = new Subject();
        subject.setId(null); // Explicitly set ID to null to ensure it's a new entity
        subject.setName(subjectRequest.getName());
        subject.setCode(subjectRequest.getCode());
        subject.setDescription(subjectRequest.getDescription());
        subject.setPrice(subjectRequest.getPrice() != null ? subjectRequest.getPrice() : 0.0);

        if (subjectRequest.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(subjectRequest.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + subjectRequest.getTeacherId()));
            subject.setTeacher(teacher);
        }

        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject updateSubject(Long id, SubjectRequest subjectRequest) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        if (!existingSubject.getCode().equals(subjectRequest.getCode()) &&
                subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Subject with code " + subjectRequest.getCode() + " already exists");
        }

        // Update fields manually to avoid ModelMapper issues
        existingSubject.setName(subjectRequest.getName());
        existingSubject.setCode(subjectRequest.getCode());
        existingSubject.setDescription(subjectRequest.getDescription());
        existingSubject.setPrice(subjectRequest.getPrice() != null ? subjectRequest.getPrice() : 0.0);

        if (subjectRequest.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(subjectRequest.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + subjectRequest.getTeacherId()));
            existingSubject.setTeacher(teacher);
        } else {
            existingSubject.setTeacher(null);
        }

        return subjectRepository.save(existingSubject);
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    public Optional<Subject> getSubjectByCode(String code) {
        return subjectRepository.findByCode(code);
    }
}
