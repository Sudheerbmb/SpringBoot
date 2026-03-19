package com.example.student_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Enrollment date is required")
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private EnrollmentType enrollmentType;

    private String grade;

    private Double finalScore;

    private String feedback;

    private LocalDateTime completedDate;

    private LocalDateTime droppedDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum EnrollmentStatus {
        ENROLLED, COMPLETED, DROPPED, IN_PROGRESS, SUSPENDED, FAILED
    }

    public enum EnrollmentType {
        REGULAR, AUDIT, CREDIT_AUDIT, TRANSFER, EXEMPTION
    }

    // Constructors
    public Enrollment() {}

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
        this.status = EnrollmentStatus.ENROLLED;
        this.enrollmentType = EnrollmentType.REGULAR;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { 
        this.status = status;
        if (status == EnrollmentStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        } else if (status == EnrollmentStatus.DROPPED) {
            this.droppedDate = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public EnrollmentType getEnrollmentType() { return enrollmentType; }
    public void setEnrollmentType(EnrollmentType enrollmentType) { this.enrollmentType = enrollmentType; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { 
        this.grade = grade;
        if (grade != null && !grade.isEmpty()) {
            this.status = EnrollmentStatus.COMPLETED;
            this.completedDate = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { 
        this.finalScore = finalScore;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { 
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public LocalDateTime getDroppedDate() { return droppedDate; }
    public void setDroppedDate(LocalDateTime droppedDate) { this.droppedDate = droppedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED || status == EnrollmentStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }

    public boolean isDropped() {
        return status == EnrollmentStatus.DROPPED;
    }

    public long getDurationInDays() {
        if (completedDate != null) {
            return java.time.Duration.between(enrollmentDate, completedDate).toDays();
        } else if (droppedDate != null) {
            return java.time.Duration.between(enrollmentDate, droppedDate).toDays();
        } else {
            return java.time.Duration.between(enrollmentDate, LocalDateTime.now()).toDays();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}
