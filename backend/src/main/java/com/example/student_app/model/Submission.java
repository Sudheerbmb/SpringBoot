package com.example.student_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    private Integer attemptNumber;

    private Double score;

    private String feedback;

    private String gradedBy;

    private LocalDateTime gradedDate;

    private LocalDateTime submittedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum SubmissionStatus {
        DRAFT, SUBMITTED, GRADED, RETURNED, LATE, PLAGIARISM_DETECTED
    }

    // Constructors
    public Submission() {}

    public Submission(Assignment assignment, Student student) {
        this.assignment = assignment;
        this.student = student;
        this.status = SubmissionStatus.DRAFT;
        this.attemptNumber = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { 
        this.status = status;
        if (status == SubmissionStatus.SUBMITTED && submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }

    public Double getScore() { return score; }
    public void setScore(Double score) { 
        this.score = score;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { 
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now();
    }

    public String getGradedBy() { return gradedBy; }
    public void setGradedBy(String gradedBy) { 
        this.gradedBy = gradedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getGradedDate() { return gradedDate; }
    public void setGradedDate(LocalDateTime gradedDate) { 
        this.gradedDate = gradedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isSubmitted() {
        return status == SubmissionStatus.SUBMITTED || status == SubmissionStatus.GRADED || status == SubmissionStatus.RETURNED;
    }

    public boolean isGraded() {
        return status == SubmissionStatus.GRADED || status == SubmissionStatus.RETURNED;
    }

    public boolean isLate() {
        if (assignment == null || assignment.getDueDate() == null) return false;
        return submittedAt != null && submittedAt.isAfter(assignment.getDueDate());
    }

    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }

    public Double getPercentageScore() {
        if (score == null || assignment == null || assignment.getTotalPoints() == null) return null;
        return (score / assignment.getTotalPoints()) * 100;
    }

    public long getDaysLate() {
        if (!isLate() || assignment == null || assignment.getDueDate() == null || submittedAt == null) return 0;
        return java.time.Duration.between(assignment.getDueDate(), submittedAt).toDays();
    }

    public void submit() {
        this.status = SubmissionStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void grade(String gradedBy, Double score, String feedback) {
        this.status = SubmissionStatus.GRADED;
        this.gradedBy = gradedBy;
        this.score = score;
        this.feedback = feedback;
        this.gradedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
