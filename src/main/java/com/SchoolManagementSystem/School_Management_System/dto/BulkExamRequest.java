package com.SchoolManagementSystem.School_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkExamRequest {
    private Long subjectId;
    private List<StudentExamData> studentExams;
    private String examDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentExamData {
        private Long studentId;
        private Integer totalMarks;
        private Integer obtainedMarks;
    }
}

