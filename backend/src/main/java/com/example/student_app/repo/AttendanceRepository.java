package com.example.student_app.repo;

import com.example.student_app.model.Attendance;
import com.example.student_app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByStudent(Student student);
    
    List<Attendance> findByStudentId(Long studentId);
    
    List<Attendance> findByStudentAndCourse(Student student, String course);
    
    List<Attendance> findByStudentAndAttendanceDateBetween(Student student, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    Long countPresentByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    Long countTotalClassesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId ORDER BY a.attendanceDate DESC")
    List<Attendance> findRecentAttendanceByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.attendanceDate >= :startDate")
    List<Attendance> findAttendanceSince(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate);
}
