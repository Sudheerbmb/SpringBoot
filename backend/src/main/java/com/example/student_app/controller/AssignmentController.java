package com.example.student_app.controller;

import com.example.student_app.model.Assignment;
import com.example.student_app.model.Submission;
import com.example.student_app.repo.AssignmentRepository;
import com.example.student_app.repo.SubmissionRepository;
import com.example.student_app.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public AssignmentController(AssignmentRepository assignmentRepository, 
                              SubmissionRepository submissionRepository,
                              CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
    }

    // GET ALL ASSIGNMENTS
    @GetMapping
    public List<Assignment> getAll() {
        return assignmentRepository.findAll();
    }

    // GET ALL ASSIGNMENTS WITH PAGINATION
    @GetMapping("/paged")
    public Page<Assignment> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return assignmentRepository.findAll(pageable);
    }

    // GET ASSIGNMENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getById(@PathVariable Long id) {
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        return assignment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE ASSIGNMENT
    @PostMapping
    public ResponseEntity<Assignment> create(@Valid @RequestBody Assignment assignment) {
        // Validate course exists
        if (!courseRepository.existsById(assignment.getCourse().getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if assignment with same title exists for this course
        if (assignmentRepository.existsByCourseIdAndTitle(assignment.getCourse().getId(), assignment.getTitle())) {
            return ResponseEntity.badRequest().build();
        }
        
        Assignment savedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(savedAssignment);
    }

    // UPDATE ASSIGNMENT
    @PutMapping("/{id}")
    public ResponseEntity<Assignment> update(@PathVariable Long id, @Valid @RequestBody Assignment assignment) {
        if (!assignmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        assignment.setId(id);
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(updatedAssignment);
    }

    // DELETE ASSIGNMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!assignmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if there are any submissions
        long submissionCount = submissionRepository.countSubmissionsByAssignment(id);
        if (submissionCount > 0) {
            return ResponseEntity.badRequest().build();
        }
        
        assignmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET ASSIGNMENTS BY COURSE
    @GetMapping("/course/{courseId}")
    public List<Assignment> getByCourse(@PathVariable Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    // GET ASSIGNMENTS BY COURSE ORDERED BY DUE DATE
    @GetMapping("/course/{courseId}/ordered")
    public List<Assignment> getByCourseOrdered(@PathVariable Long courseId) {
        return assignmentRepository.findAssignmentsByCourseOrderByDueDate(courseId);
    }

    // GET ASSIGNMENTS BY STATUS
    @GetMapping("/status/{status}")
    public List<Assignment> getByStatus(@PathVariable Assignment.AssignmentStatus status) {
        return assignmentRepository.findByStatus(status);
    }

    // GET ASSIGNMENTS BY TYPE
    @GetMapping("/type/{type}")
    public List<Assignment> getByType(@PathVariable Assignment.AssignmentType type) {
        return assignmentRepository.findByType(type);
    }

    // GET AVAILABLE ASSIGNMENTS
    @GetMapping("/available")
    public List<Assignment> getAvailableAssignments() {
        return assignmentRepository.findAvailableAssignments(LocalDateTime.now());
    }

    // GET OVERDUE ASSIGNMENTS
    @GetMapping("/overdue")
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now());
    }

    // GET ASSIGNMENTS DUE BETWEEN DATES
    @GetMapping("/due-between")
    public List<Assignment> getAssignmentsDueBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        return assignmentRepository.findAssignmentsDueBetween(start, end);
    }

    // GET ASSIGNMENT SUBMISSIONS
    @GetMapping("/{id}/submissions")
    public List<Submission> getAssignmentSubmissions(@PathVariable Long id) {
        return submissionRepository.findByAssignmentId(id);
    }

    // GET UNRUGRADED SUBMISSIONS FOR ASSIGNMENT
    @GetMapping("/{id}/ungraded")
    public List<Submission> getUngradedSubmissions(@PathVariable Long id) {
        return submissionRepository.findUngradedSubmissionsByAssignment(id);
    }

    // GET ASSIGNMENT STATISTICS
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getAssignmentStats(@PathVariable Long id) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Assignment assignment = assignmentOpt.get();
        
        long totalSubmissions = submissionRepository.countSubmissionsByAssignment(id);
        long submittedSubmissions = submissionRepository.countSubmittedSubmissionsByAssignment(id);
        Double averageScore = assignmentRepository.getAveragePointsByCourse(assignment.getCourse().getId());
        
        var stats = new Object() {
            public final Long assignmentId = assignment.getId();
            public final String title = assignment.getTitle();
            public final Assignment.AssignmentType type = assignment.getType();
            public final Integer totalPoints = assignment.getTotalPoints();
            public final LocalDateTime dueDate = assignment.getDueDate();
            public final Assignment.AssignmentStatus status = assignment.getStatus();
            public final long totalSubmissions = totalSubmissions;
            public final long submittedSubmissions = submittedSubmissions;
            public final long ungradedSubmissions = totalSubmissions - submittedSubmissions;
            public final double submissionRate = totalSubmissions > 0 ? (double) submittedSubmissions / totalSubmissions * 100 : 0;
            public final boolean isOverdue = assignment.isOverdue();
            public final boolean isAvailable = assignment.isAvailable();
            public final long daysUntilDue = assignment.getDaysUntilDue();
            public final boolean hasTimeLimit = assignment.hasTimeLimit();
            public final boolean allowsLateSubmission = assignment.getAllowLateSubmission();
        };
        
        return ResponseEntity.ok(stats);
    }

    // PUBLISH ASSIGNMENT
    @PostMapping("/{id}/publish")
    public ResponseEntity<Assignment> publishAssignment(@PathVariable Long id) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Assignment assignment = assignmentOpt.get();
        assignment.setStatus(Assignment.AssignmentStatus.PUBLISHED);
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        
        return ResponseEntity.ok(updatedAssignment);
    }

    // CLOSE ASSIGNMENT
    @PostMapping("/{id}/close")
    public ResponseEntity<Assignment> closeAssignment(@PathVariable Long id) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Assignment assignment = assignmentOpt.get();
        assignment.setStatus(Assignment.AssignmentStatus.CLOSED);
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        
        return ResponseEntity.ok(updatedAssignment);
    }

    // SEARCH ASSIGNMENTS
    @GetMapping("/search")
    public List<Assignment> search(@RequestParam String keyword) {
        return assignmentRepository.findByKeyword(keyword);
    }

    // GET ASSIGNMENTS BY INSTRUCTOR
    @GetMapping("/instructor/{instructor}")
    public List<Assignment> getByInstructor(@PathVariable String instructor) {
        return assignmentRepository.findByInstructor(instructor);
    }

    // GET UPCOMING ASSIGNMENTS
    @GetMapping("/upcoming")
    public List<Assignment> getUpcomingAssignments(
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        return assignmentRepository.findUpcomingAssignments(pageable);
    }
}
