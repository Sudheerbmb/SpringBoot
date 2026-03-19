package com.example.student_app.service;

import com.example.student_app.model.Student;
import com.example.student_app.model.Grade;
import com.example.student_app.model.Attendance;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIStudentService {

    private final ChatClient chatClient;

    @Autowired
    public AIStudentService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultSystem("""
                    You are an intelligent student academic advisor and assistant. Your role is to:
                    1. Analyze student performance and provide personalized recommendations
                    2. Help students understand their academic progress
                    3. Suggest study strategies and course recommendations
                    4. Provide insights on attendance patterns and their impact
                    5. Generate comprehensive academic reports
                    Always be encouraging, constructive, and data-driven in your responses.
                """)
                .build();
    }

    public String analyzeStudentPerformance(Student student, List<Grade> grades, List<Attendance> attendance) {
        String performanceSummary = generatePerformanceSummary(student, grades, attendance);
        
        return chatClient.prompt()
                .user(String.format("""
                    Analyze this student's academic performance and provide comprehensive insights:
                    
                    Student Information:
                    %s
                    
                    Performance Summary:
                    %s
                    
                    Please provide:
                    1. Overall performance assessment
                    2. Strengths and areas for improvement
                    3. Specific recommendations for academic success
                    4. Study strategies based on their performance patterns
                    5. Course recommendations for upcoming semesters
                    6. Attendance impact analysis if relevant
                    """, formatStudentInfo(student), performanceSummary))
                .call()
                .content();
    }

    public String generatePersonalizedRecommendations(Student student, List<Grade> grades) {
        Map<String, Double> subjectPerformance = grades.stream()
                .collect(Collectors.groupingBy(
                    Grade::getSubject,
                    Collectors.averagingDouble(Grade::getScore)
                ));

        return chatClient.prompt()
                .user(String.format("""
                    Based on this student's performance by subject, provide personalized recommendations:
                    
                    Student: %s, GPA: %.2f
                    Subject Performance: %s
                    
                    Provide recommendations for:
                    1. Study focus areas
                    2. Time management strategies
                    3. Additional resources or support needed
                    4. Course load adjustments for next semester
                    """, 
                    student.getName(), 
                    student.getGpa() != null ? student.getGpa() : 0.0,
                    formatSubjectPerformance(subjectPerformance)))
                .call()
                .content();
    }

    public String chatWithStudentAdvisor(String studentQuery, Student student, List<Grade> grades) {
        String context = String.format("""
            Current student context:
            - Name: %s
            - Major: %s
            - GPA: %.2f
            - Current Semester: %s
            - Recent Grades: %s
            """, 
            student.getName(),
            student.getMajor() != null ? student.getMajor() : "Undeclared",
            student.getGpa() != null ? student.getGpa() : 0.0,
            student.getSemester() != null ? student.getSemester() : "Unknown",
            formatRecentGrades(grades));

        return chatClient.prompt()
                .system("You are a helpful academic advisor. Use the student's context to provide personalized advice.")
                .user(String.format("Student Context: %s\n\nStudent Question: %s", context, studentQuery))
                .call()
                .content();
    }

    public String generateAcademicReport(Student student, List<Grade> grades, List<Attendance> attendance) {
        String comprehensiveSummary = generateComprehensiveSummary(student, grades, attendance);
        
        return chatClient.prompt()
                .user(String.format("""
                    Generate a comprehensive academic report for this student:
                    
                    %s
                    
                    Format the report with:
                    1. Executive Summary
                    2. Academic Performance Analysis
                    3. Attendance Overview
                    4. Strengths and Achievements
                    5. Areas for Improvement
                    6. Recommendations
                    7. Future Outlook
                    """, comprehensiveSummary))
                .call()
                .content();
    }

    public String predictAcademicSuccess(Student student, List<Grade> grades) {
        return chatClient.prompt()
                .user(String.format("""
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
                    formatAcademicHistory(grades)))
                .call()
                .content();
    }

    private String generatePerformanceSummary(Student student, List<Grade> grades, List<Attendance> attendance) {
        StringBuilder summary = new StringBuilder();
        
        // Grade analysis
        if (!grades.isEmpty()) {
            double avgScore = grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
            Map<String, Long> gradeDistribution = grades.stream()
                .collect(Collectors.groupingBy(Grade::getGradeLetter, Collectors.counting()));
            
            summary.append(String.format("Average Score: %.2f\\n", avgScore));
            summary.append("Grade Distribution: ").append(gradeDistribution).append("\\n");
        }
        
        // Attendance analysis
        if (!attendance.isEmpty()) {
            long totalClasses = attendance.size();
            long presentCount = attendance.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
            double attendanceRate = (double) presentCount / totalClasses * 100;
            
            summary.append(String.format("Attendance Rate: %.1f%%\\n", attendanceRate));
        }
        
        return summary.toString();
    }

    private String formatStudentInfo(Student student) {
        return String.format("""
            Name: %s
            Email: %s
            Major: %s
            Current GPA: %.2f
            Credits Completed: %d
            Current Semester: %s
            Status: %s
            Enrollment Date: %s
            """,
            student.getName(),
            student.getEmail(),
            student.getMajor() != null ? student.getMajor() : "Undeclared",
            student.getGpa() != null ? student.getGpa() : 0.0,
            student.getCreditsCompleted() != null ? student.getCreditsCompleted() : 0,
            student.getSemester() != null ? student.getSemester() : "Unknown",
            student.getStatus() != null ? student.getStatus() : "UNKNOWN",
            student.getEnrollmentDate() != null ? student.getEnrollmentDate() : "Unknown"
        );
    }

    private String formatSubjectPerformance(Map<String, Double> subjectPerformance) {
        return subjectPerformance.entrySet().stream()
            .map(entry -> String.format("%s: %.1f%%", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(", "));
    }

    private String formatRecentGrades(List<Grade> grades) {
        return grades.stream()
            .sorted((a, b) -> b.getGradedAt().compareTo(a.getGradedAt()))
            .limit(5)
            .map(g -> String.format("%s (%s): %.1f%%", g.getSubject(), g.getGradeLetter(), g.getScore()))
            .collect(Collectors.joining(", "));
    }

    private String generateComprehensiveSummary(Student student, List<Grade> grades, List<Attendance> attendance) {
        return formatStudentInfo(student) + "\\n\\n" + generatePerformanceSummary(student, grades, attendance);
    }

    private String formatAcademicHistory(List<Grade> grades) {
        return grades.stream()
            .sorted((a, b) -> a.getGradedAt().compareTo(b.getGradedAt()))
            .map(g -> String.format("%s %s: %.1f%% (%s)", 
                g.getAcademicYear(), g.getSemester(), g.getScore(), g.getSubject()))
            .collect(Collectors.joining("\\n"));
    }
}
