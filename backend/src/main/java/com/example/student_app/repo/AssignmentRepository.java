package com.example.student_app.repo;

import com.example.student_app.model.Assignment;
import com.example.student_app.model.Assignment.AssignmentStatus;
import com.example.student_app.model.Assignment.AssignmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseId(Long courseId);

    List<Assignment> findByStatus(AssignmentStatus status);

    List<Assignment> findByType(AssignmentType type);

    List<Assignment> findByCourseIdAndStatus(Long courseId, AssignmentStatus status);

    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.dueDate >= :startDate AND a.dueDate <= :endDate")
    List<Assignment> findByCourseIdAndDueDateBetween(@Param("courseId") Long courseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.status = 'PUBLISHED'")
    List<Assignment> findOverdueAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN :startDate AND :endDate")
    List<Assignment> findAssignmentsDueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Assignment a WHERE a.availableFrom <= :now AND (a.availableUntil IS NULL OR a.availableUntil >= :now) AND a.status = 'PUBLISHED'")
    List<Assignment> findAvailableAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Assignment a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Assignment> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT a FROM Assignment a WHERE a.course.instructor = :instructor")
    List<Assignment> findByInstructor(@Param("instructor") String instructor);

    @Query("SELECT a.type, COUNT(a) FROM Assignment a GROUP BY a.type")
    List<Object[]> countAssignmentsByType();

    @Query("SELECT a.status, COUNT(a) FROM Assignment a GROUP BY a.status")
    List<Object[]> countAssignmentsByStatus();

    @Query("SELECT a FROM Assignment a WHERE a.allowLateSubmission = true")
    List<Assignment> findAssignmentsAllowingLateSubmission();

    @Query("SELECT a FROM Assignment a WHERE a.timeLimit IS NOT NULL")
    List<Assignment> findTimedAssignments();

    @Query("SELECT a FROM Assignment a WHERE a.maxAttempts IS NOT NULL")
    List<Assignment> findAssignmentsWithMaxAttempts();

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.course.id = :courseId AND a.status = 'PUBLISHED'")
    long countPublishedAssignmentsByCourse(@Param("courseId") Long courseId);

    @Query("SELECT AVG(a.totalPoints) FROM Assignment a WHERE a.course.id = :courseId")
    Double getAveragePointsByCourse(@Param("courseId") Long courseId);

    @Query("SELECT a FROM Assignment a WHERE a.createdBy = :createdBy")
    List<Assignment> findByCreatedBy(@Param("createdBy") String createdBy);

    @Query("SELECT a FROM Assignment a ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingAssignments(Pageable pageable);

    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId ORDER BY a.dueDate ASC")
    List<Assignment> findAssignmentsByCourseOrderByDueDate(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.dueDate < :now AND a.status = 'PUBLISHED'")
    long countOverdueAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Assignment a WHERE a.course.id IN (SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId)")
    List<Assignment> findAssignmentsByStudent(@Param("studentId") Long studentId);

    boolean existsByCourseIdAndTitle(Long courseId, String title);
}
