package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.StudentRequest;
import com.SchoolManagementSystem.School_Management_System.model.Student;
import com.SchoolManagementSystem.School_Management_System.model.Subject;
import com.SchoolManagementSystem.School_Management_System.repository.StudentRepository;
import com.SchoolManagementSystem.School_Management_System.repository.SubjectRepository;
import com.SchoolManagementSystem.School_Management_System.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final FeeRepository feeRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        // Force loading of subjects to avoid LazyInitializationException
        students.forEach(student -> {
            if (student.getSubjects() != null) {
                student.getSubjects().size(); // Trigger lazy loading
            }
        });
        return students;
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Student createStudent(StudentRequest studentRequest) {
        if (studentRepository.existsByEmail(studentRequest.getEmail())) {
            throw new RuntimeException("Student with email " + studentRequest.getEmail() + " already exists");
        }

        Student student = modelMapper.map(studentRequest, Student.class);

        // Assign subjects if provided
        if (studentRequest.getSubjectIds() != null && !studentRequest.getSubjectIds().isEmpty()) {
            List<Subject> subjects = new ArrayList<>();
            for (Long subjectId : studentRequest.getSubjectIds()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
                subjects.add(subject);
            }
            student.setSubjects(subjects);
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, StudentRequest studentRequest) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        if (!existingStudent.getEmail().equals(studentRequest.getEmail()) &&
                studentRepository.existsByEmail(studentRequest.getEmail())) {
            throw new RuntimeException("Student with email " + studentRequest.getEmail() + " already exists");
        }

        // Update basic fields
        existingStudent.setName(studentRequest.getName());
        existingStudent.setEmail(studentRequest.getEmail());
        existingStudent.setPhone(studentRequest.getPhone());
        existingStudent.setGender(studentRequest.getGender());
        existingStudent.setAddress(studentRequest.getAddress());
        existingStudent.setDateOfBirth(studentRequest.getDateOfBirth());
        existingStudent.setClassName(studentRequest.getClassName());
        existingStudent.setParentName(studentRequest.getParentName());
        existingStudent.setParentPhone(studentRequest.getParentPhone());
        existingStudent.setDiscount(studentRequest.getDiscount() != null ? studentRequest.getDiscount() : 0.0);

        // Update subjects if provided
        if (studentRequest.getSubjectIds() != null) {
            List<Subject> subjects = new ArrayList<>();
            for (Long subjectId : studentRequest.getSubjectIds()) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
                subjects.add(subject);
            }
            existingStudent.setSubjects(subjects);
        }

        return studentRepository.save(existingStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        // Explicitly delete all fees associated with this student
        // This ensures fees are deleted even if cascade doesn't work in all scenarios
        feeRepository.deleteByStudentId(id);
        
        // Delete the student
        // The cascade = CascadeType.ALL on other relationships (exams, attendances) will handle those
        studentRepository.delete(student);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }
}
