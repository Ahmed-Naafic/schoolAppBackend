# School Management System

A comprehensive Spring Boot 3 application for managing school operations including students, teachers, subjects, exams, and attendance.

## 🚀 Technology Stack

- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **PostgreSQL**
- **Spring Security with JWT**
- **Lombok**
- **ModelMapper**
- **Maven**

## 📋 Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **Student Management**: CRUD operations for student records
- **Teacher Management**: CRUD operations for teacher records
- **Subject Management**: CRUD operations for subject records with teacher assignment
- **Exam Management**: Record and manage exam results
- **Attendance Management**: Track student attendance by subject and date
- **Global Exception Handling**: Comprehensive error handling with detailed error responses
- **Data Validation**: Input validation using Bean Validation

## 🏗️ Architecture

The application follows a layered architecture:

```
├── Controller Layer (REST APIs)
├── Service Layer (Business Logic)
├── Repository Layer (Data Access)
├── Model Layer (Entities)
└── DTO Layer (Data Transfer Objects)
```

## 📊 Database Schema

### Entities and Relationships

- **User**: Authentication and authorization
- **Student**: Student information with many-to-many relationship with subjects
- **Teacher**: Teacher information with one-to-many relationship with subjects
- **Subject**: Subject information with relationships to teachers and students
- **Exam**: Exam records linking students and subjects
- **Attendance**: Attendance records linking students, subjects, and dates

## 🔧 Setup Instructions

### Prerequisites

- Java 25 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE school_management;
```

2. Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/school_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔐 Authentication

### JWT Configuration

- **Secret Key**: Configured in `application.properties`
- **Expiration**: 24 hours (86400000 ms)
- **Algorithm**: HS256

### User Roles

- **ADMIN**: Full access to all operations
- **TEACHER**: Access to students, subjects, exams, and attendance
- **STUDENT**: Limited access (can be extended based on requirements)

## 📚 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/auth/login` | User login | Public |
| POST | `/api/auth/signup/admin` | Admin registration | Public |
| POST | `/api/auth/signup/teacher` | Teacher registration | Public |
| POST | `/api/auth/signup/student` | Student registration | Public |

### Student Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/students` | Get all students | ADMIN, TEACHER |
| GET | `/api/students/{id}` | Get student by ID | ADMIN, TEACHER |
| POST | `/api/students` | Create new student | ADMIN, TEACHER |
| PUT | `/api/students/{id}` | Update student | ADMIN, TEACHER |
| DELETE | `/api/students/{id}` | Delete student | ADMIN, TEACHER |

### Teacher Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/teachers` | Get all teachers | ADMIN |
| GET | `/api/teachers/{id}` | Get teacher by ID | ADMIN |
| POST | `/api/teachers` | Create new teacher | ADMIN |
| PUT | `/api/teachers/{id}` | Update teacher | ADMIN |
| DELETE | `/api/teachers/{id}` | Delete teacher | ADMIN |

### Subject Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/subjects` | Get all subjects | ADMIN, TEACHER |
| GET | `/api/subjects/{id}` | Get subject by ID | ADMIN, TEACHER |
| POST | `/api/subjects` | Create new subject | ADMIN, TEACHER |
| PUT | `/api/subjects/{id}` | Update subject | ADMIN, TEACHER |
| DELETE | `/api/subjects/{id}` | Delete subject | ADMIN, TEACHER |

### Exam Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/exams` | Get all exams | ADMIN, TEACHER |
| GET | `/api/exams/{id}` | Get exam by ID | ADMIN, TEACHER |
| GET | `/api/exams/student/{studentId}` | Get exams by student | ADMIN, TEACHER |
| GET | `/api/exams/subject/{subjectId}` | Get exams by subject | ADMIN, TEACHER |
| POST | `/api/exams` | Create new exam | ADMIN, TEACHER |
| PUT | `/api/exams/{id}` | Update exam | ADMIN, TEACHER |
| DELETE | `/api/exams/{id}` | Delete exam | ADMIN, TEACHER |

### Attendance Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/attendance` | Get all attendance | ADMIN, TEACHER |
| GET | `/api/attendance/{id}` | Get attendance by ID | ADMIN, TEACHER |
| GET | `/api/attendance/student/{studentId}` | Get attendance by student | ADMIN, TEACHER |
| GET | `/api/attendance/subject/{subjectId}` | Get attendance by subject | ADMIN, TEACHER |
| GET | `/api/attendance/date/{date}` | Get attendance by date | ADMIN, TEACHER |
| POST | `/api/attendance` | Create new attendance | ADMIN, TEACHER |
| PUT | `/api/attendance/{id}` | Update attendance | ADMIN, TEACHER |
| DELETE | `/api/attendance/{id}` | Delete attendance | ADMIN, TEACHER |

## 📝 Request/Response Examples

### Authentication Request
```json
{
  "email": "admin@school.com",
  "password": "password123"
}
```

### Authentication Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@school.com",
  "role": "ADMIN",
  "message": "Login successful"
}
```

### Student Request
```json
{
  "name": "John Doe",
  "email": "john.doe@student.com",
  "phone": "+1234567890",
  "gender": "Male",
  "address": "123 Main St, City",
  "dateOfBirth": "2005-01-15"
}
```

### Subject Request
```json
{
  "name": "Mathematics",
  "code": "MATH101",
  "description": "Basic Mathematics Course",
  "teacherId": 1
}
```

### Exam Request
```json
{
  "subjectId": 1,
  "studentId": 1,
  "examDate": "2024-01-15",
  "totalMarks": 100,
  "obtainedMarks": 85
}
```

### Attendance Request
```json
{
  "studentId": 1,
  "subjectId": 1,
  "date": "2024-01-15",
  "status": "PRESENT"
}
```

## 🔒 Security Headers

Include the JWT token in the Authorization header for protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

## 🚨 Error Handling

The application provides comprehensive error handling with detailed error responses:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Student with email john.doe@student.com already exists",
  "path": "/api/students"
}
```

## 🧪 Testing

You can test the API endpoints using tools like:
- Postman
- curl
- Any REST client

### Example curl commands:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@school.com","password":"password123"}'

# Get all students (with JWT token)
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 📈 Future Enhancements

- Frontend web application
- Email notifications
- Report generation
- File upload for student photos
- Grade calculation and GPA tracking
- Parent portal
- Mobile application

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Support

For support and questions, please contact the development team or create an issue in the repository.
