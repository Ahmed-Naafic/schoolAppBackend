package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.ExamRequest;
import com.SchoolManagementSystem.School_Management_System.model.Exam;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.repository.ExamRepository;
import com.SchoolManagementSystem.School_Management_System.repository.StudentRepository;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public Exam createExam(ExamRequest examRequest) {
        Student student = studentRepository.findById(examRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + examRequest.getStudentId()));

        Subject subject = subjectRepository.findById(examRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + examRequest.getSubjectId()));

        Exam exam = new Exam();
        exam.setStudent(student);
        exam.setSubject(subject);
        exam.setExamDate(examRequest.getExamDate());
        exam.setTotalMarks(examRequest.getTotalMarks());
        exam.setObtainedMarks(examRequest.getObtainedMarks());

        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, ExamRequest examRequest) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + id));

        Student student = studentRepository.findById(examRequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + examRequest.getStudentId()));

        Subject subject = subjectRepository.findById(examRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + examRequest.getSubjectId()));

        existingExam.setStudent(student);
        existingExam.setSubject(subject);
        existingExam.setExamDate(examRequest.getExamDate());
        existingExam.setTotalMarks(examRequest.getTotalMarks());
        existingExam.setObtainedMarks(examRequest.getObtainedMarks());

        return examRepository.save(existingExam);
    }

    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new RuntimeException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    public List<Exam> getExamsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        return examRepository.findByStudent(student);
    }

    public List<Exam> getExamsBySubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        return examRepository.findBySubject(subject);
    }
}
