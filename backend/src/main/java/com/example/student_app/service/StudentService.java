package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import com.example.student_app.repo.StudentRepository;
import com.example.student_app.repo.GradeRepository;
import com.example.student_app.repo.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository repo;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public StudentService(StudentRepository repo, GradeRepository gradeRepository, AttendanceRepository attendanceRepository) {
        this.repo = repo;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    // GET ALL
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    // GET ALL WITH PAGINATION
    public Page<Student> getAllStudents(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // GET BY ID
    public Optional<Student> getStudentById(Long id) {
        return repo.findById(id);
    }

    // SEARCH STUDENTS
    public List<Student> searchStudents(String keyword) {
        return repo.searchStudents(keyword);
    }

    // GET STUDENTS BY COURSE
    public List<Student> getStudentsByCourse(String course) {
        return repo.findByCourse(course);
    }

    // GET STUDENTS BY STATUS
    public List<Student> getStudentsByStatus(Student.StudentStatus status) {
        return repo.findByStatus(status);
    }

    // CREATE / UPDATE
    public Student saveStudent(Student student) {
        return repo.save(student);
    }

    // DELETE
    public void deleteStudent(Long id) {
        repo.deleteById(id);
    }

    // GET STUDENT GRADES
    public List<Grade> getStudentGrades(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    // GET STUDENT ATTENDANCE
    public List<Attendance> getStudentAttendance(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    // GET STUDENT PERFORMANCE SUMMARY
    public StudentPerformanceSummary getStudentPerformanceSummary(Long studentId) {
        Optional<Student> studentOpt = getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            return null;
        }

        Student student = studentOpt.get();
        List<Grade> grades = getStudentGrades(studentId);
        List<Attendance> attendance = getStudentAttendance(studentId);

        Double averageScore = grades.stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);

        Long totalClasses = attendanceRepository.countTotalClassesByStudentId(studentId);
        Long presentClasses = attendanceRepository.countPresentByStudentId(studentId);
        Double attendanceRate = totalClasses > 0 ? (double) presentClasses / totalClasses * 100 : 0.0;

        return new StudentPerformanceSummary(
                student,
                grades.size(),
                averageScore,
                attendanceRate.intValue(),
                totalClasses.intValue()
        );
    }

    public static class StudentPerformanceSummary {
        private final Student student;
        private final int totalGrades;
        private final double averageScore;
        private final int attendanceRate;
        private final int totalClasses;

        public StudentPerformanceSummary(Student student, int totalGrades, double averageScore, int attendanceRate, int totalClasses) {
            this.student = student;
            this.totalGrades = totalGrades;
            this.averageScore = averageScore;
            this.attendanceRate = attendanceRate;
            this.totalClasses = totalClasses;
        }

        public Student getStudent() { return student; }
        public int getTotalGrades() { return totalGrades; }
        public double getAverageScore() { return averageScore; }
        public int getAttendanceRate() { return attendanceRate; }
        public int getTotalClasses() { return totalClasses; }
    }
}