package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.repo.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public void saveStudent(Student student) {
        repo.save(student);
    }

    public void deleteStudent(Long id) {
        repo.deleteById(id);
    }
}