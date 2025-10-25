package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.SubjectRequest;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.model.Teacher;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import com.SchoolManagementSystem.School_Management_System.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public Subject createSubject(SubjectRequest subjectRequest) {
        if (subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Subject with code " + subjectRequest.getCode() + " already exists");
        }

        Subject subject = modelMapper.map(subjectRequest, Subject.class);

        if (subjectRequest.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(subjectRequest.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + subjectRequest.getTeacherId()));
            subject.setTeacher(teacher);
        }

        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, SubjectRequest subjectRequest) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        if (!existingSubject.getCode().equals(subjectRequest.getCode()) &&
                subjectRepository.existsByCode(subjectRequest.getCode())) {
            throw new RuntimeException("Subject with code " + subjectRequest.getCode() + " already exists");
        }

        modelMapper.map(subjectRequest, existingSubject);

        if (subjectRequest.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(subjectRequest.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + subjectRequest.getTeacherId()));
            existingSubject.setTeacher(teacher);
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
