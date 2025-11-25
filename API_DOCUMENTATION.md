# Spring Boot Backend API Documentation

## Overview
Complete Spring Boot backend for School Management System with PostgreSQL database.

## Database Configuration
- **Database**: PostgreSQL
- **Connection**: `jdbc:postgresql://localhost:5432/school_management`
- **Username**: `postgres`
- **Password**: `root`
- **DDL Mode**: `update` (auto-creates/updates tables)

## API Endpoints

### Students (`/api/students`)
- `GET /api/students` - Get all students
- `GET /api/students/class/{className}` - Get students by class
- `GET /api/students/{id}` - Get student by ID
- `POST /api/students` - Create student (with parent info, subjects, discount)
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

**StudentRequest Fields:**
- name (required)
- email (required)
- phone
- gender
- address
- dateOfBirth
- class (className)
- parentName
- parentPhone
- discount (Double)
- subjectIds (List<Long>)

### Subjects (`/api/subjects`)
- `GET /api/subjects` - Get all subjects
- `GET /api/subjects/{id}` - Get subject by ID
- `POST /api/subjects` - Create subject (with price)
- `PUT /api/subjects/{id}` - Update subject
- `DELETE /api/subjects/{id}` - Delete subject

**SubjectRequest Fields:**
- name (required)
- code (required)
- description
- teacherId
- price (Double)

### Exams (`/api/exams`)
- `GET /api/exams` - Get all exams
- `GET /api/exams/{id}` - Get exam by ID
- `GET /api/exams/student/{studentId}` - Get exams by student
- `GET /api/exams/subject/{subjectId}` - Get exams by subject
- `POST /api/exams` - Create single exam
- `POST /api/exams/bulk` - Create bulk exams (for multiple students)
- `PUT /api/exams/{id}` - Update exam
- `DELETE /api/exams/{id}` - Delete exam

**BulkExamRequest:**
```json
{
  "subjectId": 1,
  "examDate": "2024-01-15",
  "studentExams": [
    {
      "studentId": 1,
      "totalMarks": 100,
      "obtainedMarks": 85
    }
  ]
}
```

### Fees (`/api/fees`)
- `GET /api/fees` - Get all fees
- `GET /api/fees/student/{studentId}` - Get fees by student
- `GET /api/fees/subject/{subjectId}` - Get fees by subject
- `POST /api/fees` - Create fee(s) (supports multiple subjects)
- `DELETE /api/fees/{id}` - Delete fee

**FeeRequest:**
- studentId (required)
- subjectIds (List<Long>, required)
- totalAmount (required)
- discountType ("percentage" or "fixed")
- discountValue (Double)
- finalAmount (required)
- paymentDate (LocalDate, required)

### Attendance (`/api/attendance`)
- `GET /api/attendance` - Get all attendance
- `GET /api/attendance/{id}` - Get attendance by ID
- `GET /api/attendance/student/{studentId}` - Get attendance by student
- `GET /api/attendance/subject/{subjectId}` - Get attendance by subject
- `GET /api/attendance/date/{date}` - Get attendance by date
- `POST /api/attendance` - Create single attendance
- `POST /api/attendance/bulk` - Create bulk attendance (for multiple students)
- `PUT /api/attendance/{id}` - Update attendance
- `DELETE /api/attendance/{id}` - Delete attendance

**BulkAttendanceRequest:**
```json
{
  "subjectId": 1,
  "date": "2024-01-15",
  "studentAttendances": [
    {
      "studentId": 1,
      "status": "PRESENT"
    }
  ]
}
```

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard/statistics` - Get dashboard statistics
- `GET /api/dashboard/charts/exam-performance` - Get exam performance chart data

**Dashboard Statistics Response:**
```json
{
  "totalStudents": 50,
  "totalTeachers": 10,
  "totalSubjects": 15,
  "totalExams": 200,
  "totalAttendance": 500,
  "totalFeesCollected": 50000.0,
  "passedExams": 150,
  "failedExams": 50,
  "presentCount": 450,
  "absentCount": 50,
  "attendanceRate": 90.0
}
```

## Models

### Student
- id, name, email, phone, gender, address, dateOfBirth
- **className** (stored as "class" in DB, serialized as "class" in JSON)
- parentName, parentPhone, discount
- subjects (ManyToMany), exams, attendances, fees (OneToMany)

### Subject
- id, name, code, description, **price**
- teacher (ManyToOne)
- students, exams, attendances, fees (OneToMany)

### Exam
- id, subject, student, examDate, totalMarks, obtainedMarks

### Fee
- id, student, subject, totalAmount, discountType, discountValue, finalAmount, paymentDate, status

### Attendance
- id, student, subject, date, status (PRESENT/ABSENT)

## Security
- JWT-based authentication
- Role-based access control:
  - ADMIN: Full access
  - TEACHER: Students, Subjects, Exams, Attendance
  - STUDENT: View own exams and attendance

## Notes
- All endpoints return JSON
- Proper HTTP status codes (200, 201, 204, 400, 404)
- Validation using Jakarta Validation
- CORS enabled for all origins
- Transaction management for bulk operations
- Lazy loading for relationships




