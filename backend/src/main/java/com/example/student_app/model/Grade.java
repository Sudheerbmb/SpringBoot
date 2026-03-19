package com.example.student_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @DecimalMin(value = "0.0", message = "Grade cannot be negative")
    @DecimalMax(value = "100.0", message = "Grade cannot exceed 100")
    private Double score;

    @NotBlank(message = "Grade letter is required")
    @Size(min = 1, max = 2, message = "Grade letter must be 1-2 characters")
    private String gradeLetter;

    @Min(value = 0, message = "Credits cannot be negative")
    private Integer credits;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotNull(message = "Academic year is required")
    private String academicYear;

    @Enumerated(EnumType.STRING)
    private GradeType gradeType;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime gradedAt;

    public enum GradeType {
        EXAM, ASSIGNMENT, PROJECT, QUIZ, LAB, FINAL
    }

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public String getSubject() { return subject; }
    public String getCourseCode() { return courseCode; }
    public Double getScore() { return score; }
    public String getGradeLetter() { return gradeLetter; }
    public Integer getCredits() { return credits; }
    public String getSemester() { return semester; }
    public String getAcademicYear() { return academicYear; }
    public GradeType getGradeType() { return gradeType; }
    public String getFeedback() { return feedback; }
    public LocalDateTime getGradedAt() { return gradedAt; }

    public void setId(Long id) { this.id = id; }
    public void setStudent(Student student) { this.student = student; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setScore(Double score) { this.score = score; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public void setGradeType(GradeType gradeType) { this.gradeType = gradeType; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }

    @PrePersist
    protected void onCreate() {
        gradedAt = LocalDateTime.now();
    }
}
