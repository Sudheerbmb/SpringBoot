package com.example.student_app.repo;

import com.example.student_app.model.Enrollment;
import com.example.student_app.model.Enrollment.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status IN :statuses")
    List<Enrollment> findByStudentIdAndStatusIn(@Param("studentId") Long studentId, @Param("statuses") List<EnrollmentStatus> statuses);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByEnrollmentDateBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.department = :department")
    List<Enrollment> findByStudentIdAndDepartment(@Param("studentId") Long studentId, @Param("department") String department);

    @Query("SELECT e FROM Enrollment e WHERE e.course.instructor = :instructor")
    List<Enrollment> findByInstructor(@Param("instructor") String instructor);

    @Query("SELECT e FROM Enrollment e WHERE e.grade IS NOT NULL ORDER BY e.finalScore DESC")
    List<Enrollment> findTopPerformingStudents(Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'COMPLETED' AND e.finalScore < :threshold")
    List<Enrollment> findStudentsNeedingAttention(@Param("threshold") Double threshold);

    @Query("SELECT e.course.department, COUNT(e) FROM Enrollment e GROUP BY e.course.department")
    List<Object[]> countEnrollmentsByDepartment();

    @Query("SELECT e.status, COUNT(e) FROM Enrollment e GROUP BY e.status")
    List<Object[]> countEnrollmentsByStatus();

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.level = :level")
    List<Enrollment> findByStudentIdAndCourseLevel(@Param("studentId") Long studentId, @Param("level") com.example.student_app.model.Course.CourseLevel level);

    @Query("SELECT AVG(e.finalScore) FROM Enrollment e WHERE e.course.id = :courseId AND e.finalScore IS NOT NULL")
    Double getAverageScoreByCourse(@Param("courseId") Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentType = :type")
    List<Enrollment> findByEnrollmentType(@Param("type") Enrollment.EnrollmentType type);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'COMPLETED'")
    long countCompletedCoursesByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(c.credits) FROM Enrollment e JOIN e.course c WHERE e.student.id = :studentId AND e.status = 'COMPLETED'")
    Integer getTotalCreditsByStudent(@Param("studentId") Long studentId);
}
