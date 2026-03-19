package com.example.student_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private AssignmentType type;

    @NotNull(message = "Total points are required")
    @Min(value = 1, message = "Total points must be at least 1")
    @Max(value = 1000, message = "Total points must not exceed 1000")
    private Integer totalPoints;

    private LocalDateTime dueDate;

    private LocalDateTime availableFrom;

    private LocalDateTime availableUntil;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private Integer timeLimit; // in minutes

    private Integer maxAttempts;

    private Boolean allowLateSubmission;

    private Double latePenaltyPercentage;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Submission> submissions = new HashSet<>();

    public enum AssignmentType {
        QUIZ, EXAM, ASSIGNMENT, PROJECT, PRESENTATION, LAB, HOMEWORK, ESSAY
    }

    public enum AssignmentStatus {
        DRAFT, PUBLISHED, CLOSED, ARCHIVED
    }

    // Constructors
    public Assignment() {}

    public Assignment(Course course, String title, String description, AssignmentType type, Integer totalPoints) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.type = type;
        this.totalPoints = totalPoints;
        this.status = AssignmentStatus.DRAFT;
        this.allowLateSubmission = true;
        this.latePenaltyPercentage = 10.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AssignmentType getType() { return type; }
    public void setType(AssignmentType type) { this.type = type; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }

    public LocalDateTime getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDateTime availableUntil) { this.availableUntil = availableUntil; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public Boolean getAllowLateSubmission() { return allowLateSubmission; }
    public void setAllowLateSubmission(Boolean allowLateSubmission) { this.allowLateSubmission = allowLateSubmission; }

    public Double getLatePenaltyPercentage() { return latePenaltyPercentage; }
    public void setLatePenaltyPercentage(Double latePenaltyPercentage) { this.latePenaltyPercentage = latePenaltyPercentage; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(Set<Submission> submissions) { this.submissions = submissions; }

    // Helper methods
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = availableFrom == null || !now.isBefore(availableFrom);
        boolean beforeEnd = availableUntil == null || !now.isAfter(availableUntil);
        return afterStart && beforeEnd && status == AssignmentStatus.PUBLISHED;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }

    public long getDaysUntilDue() {
        if (dueDate == null) return Long.MAX_VALUE;
        return java.time.Duration.between(LocalDateTime.now(), dueDate).toDays();
    }

    public boolean hasTimeLimit() {
        return timeLimit != null && timeLimit > 0;
    }

    public boolean hasMaxAttempts() {
        return maxAttempts != null && maxAttempts > 0;
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
