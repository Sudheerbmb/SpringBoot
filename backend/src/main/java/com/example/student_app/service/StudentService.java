package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.repo.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // ✅ GET ALL
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    // ✅ GET BY ID
    public Optional<Student> getStudentById(Long id) {
        return repo.findById(id);
    }

    // ✅ CREATE / UPDATE
    public Student saveStudent(Student student) {
        return repo.save(student);
    }

    // ✅ DELETE
    public void deleteStudent(Long id) {
        repo.deleteById(id);
    }
}