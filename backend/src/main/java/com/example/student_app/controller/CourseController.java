package com.example.student_app.controller;

import com.example.student_app.model.Course;
import com.example.student_app.repo.CourseRepository;
import com.example.student_app.repo.EnrollmentRepository;
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
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // GET ALL COURSES
    @GetMapping
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    // GET ALL COURSES WITH PAGINATION
    @GetMapping("/paged")
    public Page<Course> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return courseRepository.findAll(pageable);
    }

    // GET COURSE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET COURSE BY CODE
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<Course> getByCode(@PathVariable String courseCode) {
        Optional<Course> course = courseRepository.findByCourseCode(courseCode);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE COURSE
    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            return ResponseEntity.badRequest().build();
        }
        
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }

    // UPDATE COURSE
    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable Long id, @Valid @RequestBody Course course) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        course.setId(id);
        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(updatedCourse);
    }

    // DELETE COURSE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if there are any enrollments
        long enrollmentCount = enrollmentRepository.countByCourseId(id);
        if (enrollmentCount > 0) {
            return ResponseEntity.badRequest().build();
        }
        
        courseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // SEARCH COURSES
    @GetMapping("/search")
    public List<Course> search(@RequestParam String keyword) {
        return courseRepository.findByKeyword(keyword);
    }

    // GET COURSES BY DEPARTMENT
    @GetMapping("/department/{department}")
    public List<Course> getByDepartment(@PathVariable String department) {
        return courseRepository.findByDepartment(department);
    }

    // GET COURSES BY INSTRUCTOR
    @GetMapping("/instructor/{instructor}")
    public List<Course> getByInstructor(@PathVariable String instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    // GET COURSES BY STATUS
    @GetMapping("/status/{status}")
    public List<Course> getByStatus(@PathVariable Course.CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    // GET COURSES BY LEVEL
    @GetMapping("/level/{level}")
    public List<Course> getByLevel(@PathVariable Course.CourseLevel level) {
        return courseRepository.findByLevel(level);
    }

    // GET AVAILABLE COURSES
    @GetMapping("/available")
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }

    // GET ACTIVE COURSES
    @GetMapping("/active")
    public List<Course> getActiveCourses() {
        return courseRepository.findActiveCourses(java.time.LocalDateTime.now());
    }

    // GET FULL COURSES
    @GetMapping("/full")
    public List<Course> getFullCourses() {
        return courseRepository.findFullCourses();
    }

    // GET UPCOMING COURSES
    @GetMapping("/upcoming")
    public List<Course> getUpcomingCourses() {
        return courseRepository.findUpcomingCourses(java.time.LocalDateTime.now());
    }

    // GET COMPLETED COURSES
    @GetMapping("/completed")
    public List<Course> getCompletedCourses() {
        return courseRepository.findCompletedCourses(java.time.LocalDateTime.now());
    }

    // GET COURSE ENROLLMENTS
    @GetMapping("/{id}/enrollments")
    public List<com.example.student_app.model.Enrollment> getCourseEnrollments(@PathVariable Long id) {
        return enrollmentRepository.findByCourseId(id);
    }

    // GET COURSE ENROLLMENT COUNT
    @GetMapping("/{id}/enrollment-count")
    public ResponseEntity<Long> getEnrollmentCount(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        long count = enrollmentRepository.countByCourseId(id);
        return ResponseEntity.ok(count);
    }

    // GET COURSE STATISTICS
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getCourseStats(@PathVariable Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Course course = courseOpt.get();
        
        long totalEnrollments = enrollmentRepository.countByCourseId(id);
        long activeEnrollments = enrollmentRepository.countByCourseIdAndStatus(id, com.example.student_app.model.Enrollment.EnrollmentStatus.ENROLLED);
        long completedEnrollments = enrollmentRepository.countByCourseIdAndStatus(id, com.example.student_app.model.Enrollment.EnrollmentStatus.COMPLETED);
        
        var stats = new Object() {
            public final Long courseId = course.getId();
            public final String courseName = course.getCourseName();
            public final String courseCode = course.getCourseCode();
            public final Integer capacity = course.getCapacity();
            public final Integer enrolledCount = course.getEnrolledCount();
            public final long totalEnrollments = totalEnrollments;
            public final long activeEnrollments = activeEnrollments;
            public final long completedEnrollments = completedEnrollments;
            public final double enrollmentRate = course.getEnrollmentRate();
            public final int availableSlots = course.getAvailableSlots();
            public final boolean hasCapacity = course.hasCapacity();
            public final Course.CourseStatus status = course.getStatus();
            public final Course.CourseLevel level = course.getLevel();
            public final String department = course.getDepartment();
        };
        
        return ResponseEntity.ok(stats);
    }

    // PUBLISH COURSE
    @PostMapping("/{id}/publish")
    public ResponseEntity<Course> publishCourse(@PathVariable Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Course course = courseOpt.get();
        course.setStatus(Course.CourseStatus.PUBLISHED);
        Course updatedCourse = courseRepository.save(course);
        
        return ResponseEntity.ok(updatedCourse);
    }

    // CANCEL COURSE
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Course> cancelCourse(@PathVariable Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Course course = courseOpt.get();
        course.setStatus(Course.CourseStatus.CANCELLED);
        Course updatedCourse = courseRepository.save(course);
        
        return ResponseEntity.ok(updatedCourse);
    }
}
