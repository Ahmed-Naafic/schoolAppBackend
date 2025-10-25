package com.SchoolManagementSystem.School_Management_System.service;

import com.SchoolManagementSystem.School_Management_System.dto.TeacherRequest;
import com.SchoolManagementSystem.School_Management_System.model.Teacher;
import com.SchoolManagementSystem.School_Management_System.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(TeacherRequest teacherRequest) {
        if (teacherRepository.existsByEmail(teacherRequest.getEmail())) {
            throw new RuntimeException("Teacher with email " + teacherRequest.getEmail() + " already exists");
        }

        Teacher teacher = modelMapper.map(teacherRequest, Teacher.class);
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, TeacherRequest teacherRequest) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        if (!existingTeacher.getEmail().equals(teacherRequest.getEmail()) &&
                teacherRepository.existsByEmail(teacherRequest.getEmail())) {
            throw new RuntimeException("Teacher with email " + teacherRequest.getEmail() + " already exists");
        }

        modelMapper.map(teacherRequest, existingTeacher);
        return teacherRepository.save(existingTeacher);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }
}
