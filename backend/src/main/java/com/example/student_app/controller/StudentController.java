package com.example.student_app.controller;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import com.example.student_app.service.StudentService;
import com.example.student_app.service.AIStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    private final AIStudentService aiStudentService;

    @Autowired
    public StudentController(StudentService studentService, AIStudentService aiStudentService) {
        this.studentService = studentService;
        this.aiStudentService = aiStudentService;
    }

    // GET ALL
    @GetMapping
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }

    // GET ALL WITH PAGINATION
    @GetMapping("/paged")
    public Page<Student> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return studentService.getAllStudents(pageable);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // SEARCH
    @GetMapping("/search")
    public List<Student> search(@RequestParam String keyword) {
        return studentService.searchStudents(keyword);
    }

    // GET BY COURSE
    @GetMapping("/course/{course}")
    public List<Student> getByCourse(@PathVariable String course) {
        return studentService.getStudentsByCourse(course);
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public List<Student> getByStatus(@PathVariable Student.StudentStatus status) {
        return studentService.getStudentsByStatus(status);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Student> add(@RequestBody Student student) {
        try {
            Student savedStudent = studentService.saveStudent(student);
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> existing = studentService.getStudentById(id);

        if (existing.isPresent()) {
            Student updated = existing.get();
            updated.setName(student.getName());
            updated.setEmail(student.getEmail());
            updated.setPhone(student.getPhone());
            updated.setDateOfBirth(student.getDateOfBirth());
            updated.setCourse(student.getCourse());
            updated.setMajor(student.getMajor());
            updated.setSemester(student.getSemester());
            updated.setGpa(student.getGpa());
            updated.setCreditsCompleted(student.getCreditsCompleted());
            updated.setEnrollmentDate(student.getEnrollmentDate());
            updated.setGraduationDate(student.getGraduationDate());
            updated.setStatus(student.getStatus());
            updated.setAddress(student.getAddress());
            updated.setCity(student.getCity());
            updated.setState(student.getState());
            updated.setZipCode(student.getZipCode());
            updated.setCountry(student.getCountry());
            updated.setNotes(student.getNotes());
            
            return ResponseEntity.ok(studentService.saveStudent(updated));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            studentService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET STUDENT GRADES
    @GetMapping("/{id}/grades")
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable Long id) {
        if (studentService.getStudentById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.getStudentGrades(id));
    }

    // GET STUDENT ATTENDANCE
    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable Long id) {
        if (studentService.getStudentById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.getStudentAttendance(id));
    }

    // GET STUDENT PERFORMANCE SUMMARY
    @GetMapping("/{id}/performance")
    public ResponseEntity<StudentService.StudentPerformanceSummary> getPerformanceSummary(@PathVariable Long id) {
        StudentService.StudentPerformanceSummary summary = studentService.getStudentPerformanceSummary(id);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

    // AI ANALYZE STUDENT PERFORMANCE
    @GetMapping("/{id}/ai/analyze")
    public ResponseEntity<String> analyzeStudentPerformance(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        List<Grade> grades = studentService.getStudentGrades(id);
        List<Attendance> attendance = studentService.getStudentAttendance(id);

        String analysis = aiStudentService.analyzeStudentPerformance(student, grades, attendance);
        return ResponseEntity.ok(analysis);
    }

    // AI GET PERSONALIZED RECOMMENDATIONS
    @GetMapping("/{id}/ai/recommendations")
    public ResponseEntity<String> getRecommendations(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        List<Grade> grades = studentService.getStudentGrades(id);

        String recommendations = aiStudentService.generatePersonalizedRecommendations(student, grades);
        return ResponseEntity.ok(recommendations);
    }

    // AI CHAT WITH ADVISOR
    @PostMapping("/{id}/ai/chat")
    public ResponseEntity<String> chatWithAdvisor(@PathVariable Long id, @RequestBody ChatRequest request) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        List<Grade> grades = studentService.getStudentGrades(id);

        String response = aiStudentService.chatWithStudentAdvisor(request.getMessage(), student, grades);
        return ResponseEntity.ok(response);
    }

    // AI GENERATE ACADEMIC REPORT
    @GetMapping("/{id}/ai/report")
    public ResponseEntity<String> generateAcademicReport(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        List<Grade> grades = studentService.getStudentGrades(id);
        List<Attendance> attendance = studentService.getStudentAttendance(id);

        String report = aiStudentService.generateAcademicReport(student, grades, attendance);
        return ResponseEntity.ok(report);
    }

    // AI PREDICT ACADEMIC SUCCESS
    @GetMapping("/{id}/ai/predict")
    public ResponseEntity<String> predictAcademicSuccess(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();
        List<Grade> grades = studentService.getStudentGrades(id);

        String prediction = aiStudentService.predictAcademicSuccess(student, grades);
        return ResponseEntity.ok(prediction);
    }

    // DTO for chat requests
    public static class ChatRequest {
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}