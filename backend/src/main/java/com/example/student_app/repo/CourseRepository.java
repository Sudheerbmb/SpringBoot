package com.example.student_app.repo;

import com.example.student_app.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartment(String department);

    List<Course> findByInstructor(String instructor);

    List<Course> findByStatus(Course.CourseStatus status);

    List<Course> findByLevel(Course.CourseLevel level);

    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:keyword% OR c.courseCode LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Course> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Course c WHERE c.capacity > c.enrolledCount")
    List<Course> findAvailableCourses();

    @Query("SELECT c FROM Course c WHERE c.startDate <= :date AND c.endDate >= :date")
    List<Course> findActiveCourses(@Param("date") java.time.LocalDateTime date);

    @Query("SELECT c FROM Course c WHERE c.enrolledCount >= c.capacity")
    List<Course> findFullCourses();

    @Query("SELECT c FROM Course c ORDER BY c.enrolledCount DESC")
    List<Course> findMostPopularCourses(Pageable pageable);

    @Query("SELECT c.department, COUNT(c) FROM Course c GROUP BY c.department")
    List<Object[]> countCoursesByDepartment();

    @Query("SELECT c.level, COUNT(c) FROM Course c GROUP BY c.level")
    List<Object[]> countCoursesByLevel();

    @Query("SELECT c FROM Course c WHERE c.credits BETWEEN :min AND :max")
    List<Course> findByCreditsRange(@Param("min") Integer min, @Param("max") Integer max);

    @Query("SELECT c FROM Course c WHERE c.startDate >= :startDate")
    List<Course> findUpcomingCourses(@Param("startDate") java.time.LocalDateTime startDate);

    @Query("SELECT c FROM Course c WHERE c.endDate <= :endDate")
    List<Course> findCompletedCourses(@Param("endDate") java.time.LocalDateTime endDate);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.department = :department")
    long countByDepartment(@Param("department") String department);

    @Query("SELECT AVG(c.enrolledCount) FROM Course c")
    Double getAverageEnrollment();

    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student.id = :studentId")
    List<Course> findCoursesByStudent(@Param("studentId") Long studentId);
}
