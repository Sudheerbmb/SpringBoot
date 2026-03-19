package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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

    public Mono<String> predictAcademicSuccess(Student student, List<Grade> grades) {
        String prompt = String.format("""
                Based on this student's academic history, predict their potential for success and provide risk factors:
                
                Student: %s, Current GPA: %.2f
                Academic History: %s
                
                Analyze and provide:
                1. Success probability score (1-10)
                2. Key risk factors
                3. Success indicators
                4. Mitigation strategies for risks
                5. Long-term academic outlook
                """, 
                student.getName(),
                student.getGpa() != null ? student.getGpa() : 0.0,
                grades.stream().map(g -> g.getSubject() + ": " + g.getScore()).collect(Collectors.joining(", "))
        );
        
        return groqService.callGroqAPI(prompt);
    }
}
