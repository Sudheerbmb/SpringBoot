package com.example.student_app.controller;

import com.example.student_app.model.Student;
import com.example.student_app.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // ✅ GET ALL
    @GetMapping
    public List<Student> getAll() {
        return service.getAllStudents();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return service.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    // ✅ CREATE
    @PostMapping
    public Student add(@RequestBody Student student) {
        return service.saveStudent(student);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> existing = service.getStudentById(id);

        if (existing.isPresent()) {
            Student updated = existing.get();
            updated.setName(student.getName());
            updated.setEmail(student.getEmail());
            return service.saveStudent(updated);
        } else {
            throw new RuntimeException("Student not found with id " + id);
        }
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return "Student deleted successfully";
    }
}