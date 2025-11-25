package com.SchoolManagementSystem.School_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceRequest {
    private Long subjectId;
    private List<StudentAttendanceData> studentAttendances;
    private String date;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceData {
        private Long studentId;
        private String status; // PRESENT, ABSENT
    }
}




