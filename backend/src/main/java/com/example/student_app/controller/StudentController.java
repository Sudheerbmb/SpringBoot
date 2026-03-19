package com.example.student_app.controller;

import com.example.student_app.model.Student;
import com.example.student_app.model.Course;
import com.example.student_app.model.Enrollment;
import com.example.student_app.model.Assignment;
import com.example.student_app.model.Submission;
import com.example.student_app.model.Attendance;
import com.example.student_app.service.StudentService;
import com.example.student_app.repo.CourseRepository;
import com.example.student_app.repo.EnrollmentRepository;
import com.example.student_app.repo.AssignmentRepository;
import com.example.student_app.repo.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public StudentController(StudentService studentService, 
                           CourseRepository courseRepository,
                           EnrollmentRepository enrollmentRepository,
                           AssignmentRepository assignmentRepository,
                           SubmissionRepository submissionRepository) {
        this.studentService = studentService;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    // GET ALL STUDENTS
    @GetMapping
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }

    // GET ALL STUDENTS WITH PAGINATION
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

    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE STUDENT
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.createStudent(student);
        return ResponseEntity.ok(savedStudent);
    }

    // UPDATE STUDENT
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @Valid @RequestBody Student student) {
        Optional<Student> updatedStudent = studentService.updateStudent(id, student);
        return updatedStudent.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // SEARCH STUDENTS
    @GetMapping("/search")
    public List<Student> search(@RequestParam String keyword) {
        return studentService.searchStudents(keyword);
    }

    // GET STUDENTS BY COURSE
    @GetMapping("/course/{courseId}")
    public List<Student> getStudentsByCourse(@PathVariable Long courseId) {
        return studentService.getStudentsByCourse(courseId);
    }

    // GET STUDENT ENROLLMENTS
    @GetMapping("/{id}/enrollments")
    public List<Enrollment> getStudentEnrollments(@PathVariable Long id) {
        return enrollmentRepository.findByStudentId(id);
    }

    // GET STUDENT COURSES
    @GetMapping("/{id}/courses")
    public List<Course> getStudentCourses(@PathVariable Long id) {
        return courseRepository.findCoursesByStudent(id);
    }

    // GET STUDENT ASSIGNMENTS
    @GetMapping("/{id}/assignments")
    public List<Assignment> getStudentAssignments(@PathVariable Long id) {
        return assignmentRepository.findAssignmentsByStudent(id);
    }

    // GET STUDENT SUBMISSIONS
    @GetMapping("/{id}/submissions")
    public List<Submission> getStudentSubmissions(@PathVariable Long id) {
        return submissionRepository.findByStudentId(id);
    }

    // GET STUDENT GRADES
    @GetMapping("/{id}/grades")
    public List<Submission> getStudentGrades(@PathVariable Long id) {
        return submissionRepository.findByStudentIdAndStatus(id, Submission.SubmissionStatus.GRADED);
    }

    // ENROLL STUDENT IN COURSE
    @PostMapping("/{id}/enroll/{courseId}")
    public ResponseEntity<Enrollment> enrollInCourse(@PathVariable Long id, @PathVariable Long courseId) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Student student = studentOpt.get();
        Course course = courseOpt.get();
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(id, courseId)) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if course has capacity
        if (!course.hasCapacity()) {
            return ResponseEntity.badRequest().build();
        }
        
        Enrollment enrollment = new Enrollment(student, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        // Update course enrollment count
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);
        
        return ResponseEntity.ok(savedEnrollment);
    }

    // DROP COURSE
    @DeleteMapping("/{id}/enroll/{courseId}")
    public ResponseEntity<Void> dropCourse(@PathVariable Long id, @PathVariable Long courseId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(id, courseId);
        
        if (enrollmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus(Enrollment.EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
        
        // Update course enrollment count
        Course course = enrollment.getCourse();
        course.setEnrolledCount(Math.max(0, course.getEnrolledCount() - 1));
        courseRepository.save(course);
        
        return ResponseEntity.noContent().build();
    }

    // SUBMIT ASSIGNMENT
    @PostMapping("/{id}/submit/{assignmentId}")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long id, 
            @PathVariable Long assignmentId,
            @Valid @RequestBody Submission submission) {
        
        Optional<Student> studentOpt = studentService.getStudentById(id);
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        
        if (studentOpt.isEmpty() || assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Student student = studentOpt.get();
        Assignment assignment = assignmentOpt.get();
        
        // Check if student is enrolled in the course
        if (!enrollmentRepository.existsByStudentIdAndCourseId(id, assignment.getCourse().getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if assignment is still available
        if (!assignment.isAvailable()) {
            return ResponseEntity.badRequest().build();
        }
        
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.submit();
        
        Submission savedSubmission = submissionRepository.save(submission);
        return ResponseEntity.ok(savedSubmission);
    }

    // GET STUDENT ACADEMIC SUMMARY
    @GetMapping("/{id}/academic-summary")
    public ResponseEntity<Object> getAcademicSummary(@PathVariable Long id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Student student = studentOpt.get();
        
        // Get enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        long completedCourses = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
                .count();
        long activeCourses = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED || e.getStatus() == Enrollment.EnrollmentStatus.IN_PROGRESS)
                .count();
        
        // Get grades
        List<Submission> gradedSubmissions = submissionRepository.findByStudentIdAndStatus(id, Submission.SubmissionStatus.GRADED);
        Double averageScore = gradedSubmissions.stream()
                .filter(s -> s.getScore() != null)
                .mapToDouble(Submission::getScore)
                .average()
                .orElse(0.0);
        
        // Get total credits
        Integer totalCredits = enrollmentRepository.getTotalCreditsByStudent(id);
        
        var summary = new Object() {
            public final Long studentId = student.getId();
            public final String studentName = student.getName();
            public final String email = student.getEmail();
            public final String major = student.getMajor();
            public final Double gpa = student.getGpa();
            public final Integer creditsCompleted = student.getCreditsCompleted();
            public final Integer totalCreditsEarned = totalCredits;
            public final long completedCoursesCount = completedCourses;
            public final long activeCoursesCount = activeCourses;
            public final long totalEnrollments = enrollments.size();
            public final double averageScore = averageScore;
            public final long gradedAssignments = gradedSubmissions.size();
        };
        
        return ResponseEntity.ok(summary);
    }

    // GET STUDENT ATTENDANCE
    @GetMapping("/{id}/attendance")
    public List<Attendance> getStudentAttendance(@PathVariable Long id) {
        return studentService.getStudentAttendance(id);
    }

    // DTO for submission requests
    public static class SubmissionRequest {
        private String content;
        private String attachmentUrl;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getAttachmentUrl() { return attachmentUrl; }
        public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    }
}