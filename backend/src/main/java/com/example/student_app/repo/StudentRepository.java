package com.example.student_app.repo;

import com.example.student_app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    List<Student> findByCourse(String course);
    
    List<Student> findByStatus(Student.StudentStatus status);
    
    List<Student> findByMajor(String major);
    
    List<Student> findBySemester(String semester);
    
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.course) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.major) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Student s WHERE s.gpa >= :minGpa ORDER BY s.gpa DESC")
    List<Student> findTopPerformingStudents(@Param("minGpa") Double minGpa);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status")
    Long countByStatus(@Param("status") Student.StudentStatus status);
    
    @Query("SELECT s FROM Student s WHERE s.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Student> findStudentsByEnrollmentPeriod(@Param("startDate") java.time.LocalDate startDate, 
                                                 @Param("endDate") java.time.LocalDate endDate);
}