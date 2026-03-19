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
    public ResponseEntity<CourseStats> getCourseStats(@PathVariable Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Course course = courseOpt.get();
        
        long totalEnrollments = enrollmentRepository.countByCourseId(id);
        long activeEnrollments = enrollmentRepository.countByCourseIdAndStatus(id, com.example.student_app.model.Enrollment.EnrollmentStatus.ENROLLED);
        long completedEnrollments = enrollmentRepository.countByCourseIdAndStatus(id, com.example.student_app.model.Enrollment.EnrollmentStatus.COMPLETED);
        
        CourseStats stats = new CourseStats(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCapacity(),
                course.getEnrolledCount(),
                totalEnrollments,
                activeEnrollments,
                completedEnrollments,
                course.getEnrollmentRate(),
                course.getAvailableSlots(),
                course.hasCapacity(),
                course.getStatus(),
                course.getLevel(),
                course.getDepartment()
        );
        
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

    // DTO for course statistics
    public static class CourseStats {
        private final Long courseId;
        private final String courseName;
        private final String courseCode;
        private final Integer capacity;
        private final Integer enrolledCount;
        private final long totalEnrollments;
        private final long activeEnrollments;
        private final long completedEnrollments;
        private final double enrollmentRate;
        private final int availableSlots;
        private final boolean hasCapacity;
        private final Course.CourseStatus status;
        private final Course.CourseLevel level;
        private final String department;

        public CourseStats(Long courseId, String courseName, String courseCode, Integer capacity, Integer enrolledCount,
                          long totalEnrollments, long activeEnrollments, long completedEnrollments, double enrollmentRate,
                          int availableSlots, boolean hasCapacity, Course.CourseStatus status, Course.CourseLevel level, String department) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.courseCode = courseCode;
            this.capacity = capacity;
            this.enrolledCount = enrolledCount;
            this.totalEnrollments = totalEnrollments;
            this.activeEnrollments = activeEnrollments;
            this.completedEnrollments = completedEnrollments;
            this.enrollmentRate = enrollmentRate;
            this.availableSlots = availableSlots;
            this.hasCapacity = hasCapacity;
            this.status = status;
            this.level = level;
            this.department = department;
        }

        public Long getCourseId() { return courseId; }
        public String getCourseName() { return courseName; }
        public String getCourseCode() { return courseCode; }
        public Integer getCapacity() { return capacity; }
        public Integer getEnrolledCount() { return enrolledCount; }
        public long getTotalEnrollments() { return totalEnrollments; }
        public long getActiveEnrollments() { return activeEnrollments; }
        public long getCompletedEnrollments() { return completedEnrollments; }
        public double getEnrollmentRate() { return enrollmentRate; }
        public int getAvailableSlots() { return availableSlots; }
        public boolean isHasCapacity() { return hasCapacity; }
        public Course.CourseStatus getStatus() { return status; }
        public Course.CourseLevel getLevel() { return level; }
        public String getDepartment() { return department; }
    }
}
