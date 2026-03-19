package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroqService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key:your-groq-api-key-here}")
    private String groqApiKey;

    public GroqService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + groqApiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    public Mono<String> analyzeStudentPerformance(Student student, List<Grade> grades, List<Attendance> attendance) {
        String prompt = buildPerformanceAnalysisPrompt(student, grades, attendance);
        return callGroqAPI(prompt);
    }

    public Mono<String> getStudentRecommendations(Student student, List<Grade> grades) {
        String prompt = buildRecommendationsPrompt(student, grades);
        return callGroqAPI(prompt);
    }

    public Mono<String> chatWithStudent(String message, Student student) {
        String prompt = buildChatPrompt(message, student);
        return callGroqAPI(prompt);
    }

    public Mono<String> generateAcademicReport(Student student, List<Grade> grades, List<Attendance> attendance) {
        String prompt = buildReportPrompt(student, grades, attendance);
        return callGroqAPI(prompt);
    }

    private Mono<String> callGroqAPI(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "llama3-70b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful academic assistant for student management."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 2000
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractContentFromResponse);
    }

    private String extractContentFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error processing AI response: " + e.getMessage();
        }
    }

    private String buildPerformanceAnalysisPrompt(Student student, List<Grade> grades, List<Attendance> attendance) {
        double avgGrade = grades.stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);

        long attendanceRate = attendance.stream()
                .mapToLong(a -> a.isPresent() ? 1 : 0)
                .sum();

        return String.format("""
                Analyze the academic performance of student %s (%s).
                
                Student Details:
                - Name: %s
                - Email: %s
                - Major: %s
                - Semester: %d
                - GPA: %.2f
                
                Performance Metrics:
                - Average Grade: %.2f
                - Total Grades: %d
                - Attendance Rate: %d/%d
                
                Recent Grades: %s
                
                Please provide a comprehensive analysis of this student's performance, including strengths, areas for improvement, and overall academic standing.
                """, 
                student.getName(), student.getEmail(),
                student.getName(), student.getEmail(), student.getMajor(), student.getSemester(), student.getGpa(),
                avgGrade, grades.size(), attendanceRate, attendance.size(),
                grades.stream().map(g -> g.getSubject() + ": " + g.getScore()).collect(Collectors.joining(", "))
        );
    }

    private String buildRecommendationsPrompt(Student student, List<Grade> grades) {
        return String.format("""
                Provide personalized academic recommendations for student %s.
                
                Student Profile:
                - Name: %s
                - Major: %s
                - GPA: %.2f
                - Current Semester: %d
                
                Recent Performance: %s
                
                Please provide specific, actionable recommendations to help this student improve their academic performance.
                """,
                student.getName(),
                student.getName(), student.getMajor(), student.getGpa(), student.getSemester(),
                grades.stream().map(g -> g.getSubject() + ": " + g.getScore()).collect(Collectors.joining(", "))
        );
    }

    private String buildChatPrompt(String message, Student student) {
        return String.format("""
                A student is asking for help. Provide a helpful response.
                
                Student: %s (%s, %s, Semester %d, GPA: %.2f)
                Question: %s
                
                Please provide a helpful and encouraging response that addresses their specific question.
                """,
                student.getName(), student.getEmail(), student.getMajor(), student.getSemester(), student.getGpa(),
                message
        );
    }

    private String buildReportPrompt(Student student, List<Grade> grades, List<Attendance> attendance) {
        return String.format("""
                Generate a comprehensive academic report for student %s.
                
                Student Information:
                - Name: %s
                - Email: %s
                - Major: %s
                - Semester: %d
                - GPA: %.2f
                - Credits Completed: %d
                
                Academic Performance:
                - Grades: %s
                - Attendance: %s
                
                Please generate a formal academic report that includes:
                1. Executive Summary
                2. Academic Performance Analysis
                3. Strengths and Achievements
                4. Areas for Improvement
                5. Recommendations
                6. Future Outlook
                
                Format the report professionally and provide specific insights based on the data.
                """,
                student.getName(),
                student.getName(), student.getEmail(), student.getMajor(), student.getSemester(), 
                student.getGpa(), student.getCreditsCompleted(),
                grades.stream().map(g -> g.getSubject() + ": " + g.getScore()).collect(Collectors.joining(", ")),
                attendance.stream().map(a -> a.getDate() + ": " + (a.isPresent() ? "Present" : "Absent")).collect(Collectors.joining(", "))
        );
    }
}
