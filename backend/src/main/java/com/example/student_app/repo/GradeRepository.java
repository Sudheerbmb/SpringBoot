package com.example.student_app.repo;

import com.example.student_app.model.Grade;
import com.example.student_app.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByStudent(Student student);
    
    List<Grade> findByStudentId(Long studentId);
    
    List<Grade> findByStudentAndSemester(Student student, String semester);
    
    List<Grade> findByStudentAndAcademicYear(Student student, String academicYear);
    
    List<Grade> findBySubject(String subject);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.academicYear = :academicYear")
    List<Grade> findByStudentIdAndAcademicYear(@Param("studentId") Long studentId, @Param("academicYear") String academicYear);
    
    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.student.id = :studentId")
    Double getAverageScoreByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId ORDER BY g.gradedAt DESC")
    List<Grade> findRecentGradesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId AND g.score < 60")
    Long countFailingGradesByStudentId(@Param("studentId") Long studentId);
}
