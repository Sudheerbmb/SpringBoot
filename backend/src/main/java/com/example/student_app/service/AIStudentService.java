package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AIStudentService {

    private final GroqService groqService;

    @Autowired
    public AIStudentService(GroqService groqService) {
        this.groqService = groqService;
    }

    public Mono<String> analyzeStudentPerformance(Student student, List<Grade> grades, List<Attendance> attendance) {
        return groqService.analyzeStudentPerformance(student, grades, attendance);
    }

    public Mono<String> getStudentRecommendations(Student student, List<Grade> grades) {
        return groqService.getStudentRecommendations(student, grades);
    }

    public Mono<String> chatWithStudent(String message, Student student) {
        return groqService.chatWithStudent(message, student);
    }

    public Mono<String> generateAcademicReport(Student student, List<Grade> grades, List<Attendance> attendance) {
        return groqService.generateAcademicReport(student, grades, attendance);
    }
}
