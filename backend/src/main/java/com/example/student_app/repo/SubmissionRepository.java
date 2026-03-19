package com.example.student_app.repo;

import com.example.student_app.model.Submission;
import com.example.student_app.model.Submission.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<Submission> findByStatus(SubmissionStatus status);

    List<Submission> findByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);

    List<Submission> findByStudentIdAndStatus(Long studentId, SubmissionStatus status);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId")
    List<Submission> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId AND s.student.id = :studentId")
    List<Submission> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    @Query("SELECT s FROM Submission s WHERE s.submittedAt BETWEEN :startDate AND :endDate")
    List<Submission> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Submission s WHERE s.gradedDate BETWEEN :startDate AND :endDate")
    List<Submission> findByGradedDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.status = 'SUBMITTED'")
    List<Submission> findUngradedSubmissionsByAssignment(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM Submission s WHERE s.status = 'SUBMITTED'")
    List<Submission> findAllUngradedSubmissions();

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.instructor = :instructor AND s.status = 'SUBMITTED'")
    List<Submission> findUngradedSubmissionsByInstructor(@Param("instructor") String instructor);

    @Query("SELECT s FROM Submission s WHERE s.score IS NOT NULL ORDER BY s.score DESC")
    List<Submission> findTopScoringSubmissions(Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.score IS NOT NULL ORDER BY s.score ASC")
    List<Submission> findLowestScoringSubmissions(Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.submittedAt > s.assignment.dueDate")
    List<Submission> findLateSubmissions();

    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.submittedAt > s.assignment.dueDate")
    List<Submission> findLateSubmissionsByAssignment(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s.status, COUNT(s) FROM Submission s GROUP BY s.status")
    List<Object[]> countSubmissionsByStatus();

    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.score IS NOT NULL")
    Double getAverageScoreByAssignment(@Param("assignmentId") Long assignmentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    long countSubmissionsByAssignment(@Param("assignmentId") Long assignmentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.status = 'SUBMITTED'")
    long countSubmittedSubmissionsByAssignment(@Param("assignmentId") Long assignmentId);

    @Query("SELECT s FROM Submission s WHERE s.gradedBy = :gradedBy")
    List<Submission> findByGradedBy(@Param("gradedBy") String gradedBy);

    @Query("SELECT s FROM Submission s WHERE s.attachmentUrl IS NOT NULL")
    List<Submission> findSubmissionsWithAttachments();

    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.assignment.course.id = :courseId")
    List<Submission> findByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT s FROM Submission s WHERE s.attemptNumber > 1")
    List<Submission> findResubmissions();

    @Query("SELECT MAX(s.attemptNumber) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId")
    Integer getMaxAttemptNumber(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.status = 'LATE'")
    long countLateSubmissions();

    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.student.id = :studentId AND s.score IS NOT NULL")
    Double getAverageScoreByStudent(@Param("studentId") Long studentId);
}
