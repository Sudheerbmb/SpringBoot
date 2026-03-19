package com.example.student_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone = "";

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Course is required")
    private String course;

    private String major;
    private String semester;
    
    @DecimalMin(value = "0.0", message = "GPA cannot be negative")
    @DecimalMax(value = "4.0", message = "GPA cannot exceed 4.0")
    private Double gpa;
    
    @Min(value = 0, message = "Credits completed cannot be negative")
    private Integer creditsCompleted;
    
    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;
    
    private LocalDate graduationDate;
    
    @Enumerated(EnumType.STRING)
    private StudentStatus status;
    
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Grade> grades;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendance;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED, ON_LEAVE
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getCourse() { return course; }
    public String getMajor() { return major; }
    public String getSemester() { return semester; }
    public Double getGpa() { return gpa; }
    public Integer getCreditsCompleted() { return creditsCompleted; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public LocalDate getGraduationDate() { return graduationDate; }
    public StudentStatus getStatus() { return status; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
    public String getNotes() { return notes; }
    public List<Grade> getGrades() { return grades; }
    public List<Attendance> getAttendance() { return attendance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setCourse(String course) { this.course = course; }
    public void setMajor(String major) { this.major = major; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    public void setCreditsCompleted(Integer creditsCompleted) { this.creditsCompleted = creditsCompleted; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setGraduationDate(LocalDate graduationDate) { this.graduationDate = graduationDate; }
    public void setStatus(StudentStatus status) { this.status = status; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setCountry(String country) { this.country = country; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    public void setAttendance(List<Attendance> attendance) { this.attendance = attendance; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StudentStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}