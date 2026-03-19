package com.example.student_app.config;

import com.example.student_app.model.Student;
import com.example.student_app.model.Course;
import com.example.student_app.model.Assignment;
import com.example.student_app.model.User;
import com.example.student_app.repo.StudentRepository;
import com.example.student_app.repo.CourseRepository;
import com.example.student_app.repo.AssignmentRepository;
import com.example.student_app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if there are any students, if not, add sample data
        if (studentRepository.count() == 0) {
            initializeSampleStudents();
        }
        
        // Check if there are any courses, if not, add sample data
        if (courseRepository.count() == 0) {
            initializeSampleCourses();
        }
        
        // Check if there are any assignments, if not, add sample data
        if (assignmentRepository.count() == 0) {
            initializeSampleAssignments();
        }
        
        // Create default user for testing
        if (userRepository.count() == 0) {
            initializeDefaultUser();
        }
    }

    private void initializeSampleStudents() {
        // Create sample students
        Student student1 = new Student();
        student1.setName("John Doe");
        student1.setEmail("john.doe@example.com");
        student1.setPhone("+1234567890");
        student1.setDateOfBirth(java.time.LocalDate.of(2000, 1, 15));
        student1.setCourse("Computer Science");
        student1.setMajor("Software Engineering");
        student1.setSemester("6th");
        student1.setGpa(3.8);
        student1.setCreditsCompleted(90);
        student1.setEnrollmentDate(java.time.LocalDate.of(2021, 9, 1));
        student1.setStatus(Student.StudentStatus.ACTIVE);
        student1.setAddress("123 Main St");
        student1.setCity("New York");
        student1.setState("NY");
        student1.setZipCode("10001");
        student1.setCountry("USA");
        student1.setNotes("Excellent student, active in coding clubs");

        Student student2 = new Student();
        student2.setName("Jane Smith");
        student2.setEmail("jane.smith@example.com");
        student2.setPhone("+1234567891");
        student2.setDateOfBirth(java.time.LocalDate.of(2001, 3, 22));
        student2.setCourse("Computer Science");
        student2.setMajor("Data Science");
        student2.setSemester("4th");
        student2.setGpa(3.9);
        student2.setCreditsCompleted(60);
        student2.setEnrollmentDate(java.time.LocalDate.of(2022, 9, 1));
        student2.setStatus(Student.StudentStatus.ACTIVE);
        student2.setAddress("456 Oak Ave");
        student2.setCity("Boston");
        student2.setState("MA");
        student2.setZipCode("02101");
        student2.setCountry("USA");
        student2.setNotes("Strong analytical skills, research assistant");

        Student student3 = new Student();
        student3.setName("Mike Johnson");
        student3.setEmail("mike.johnson@example.com");
        student3.setPhone("+1234567892");
        student3.setDateOfBirth(java.time.LocalDate.of(2000, 7, 8));
        student3.setCourse("Business Administration");
        student3.setMajor("Finance");
        student3.setSemester("5th");
        student3.setGpa(3.5);
        student3.setCreditsCompleted(75);
        student3.setEnrollmentDate(java.time.LocalDate.of(2021, 9, 1));
        student3.setStatus(Student.StudentStatus.ACTIVE);
        student3.setAddress("789 Pine Rd");
        student3.setCity("Chicago");
        student3.setState("IL");
        student3.setZipCode("60601");
        student3.setCountry("USA");
        student3.setNotes("Leadership qualities, student council member");

        Student student4 = new Student();
        student4.setName("Sarah Williams");
        student4.setEmail("sarah.williams@example.com");
        student4.setPhone("+1234567893");
        student4.setDateOfBirth(java.time.LocalDate.of(2001, 11, 30));
        student4.setCourse("Engineering");
        student4.setMajor("Mechanical Engineering");
        student4.setSemester("3rd");
        student4.setGpa(3.7);
        student4.setCreditsCompleted(45);
        student4.setEnrollmentDate(java.time.LocalDate.of(2022, 9, 1));
        student4.setStatus(Student.StudentStatus.ACTIVE);
        student4.setAddress("321 Elm St");
        student4.setCity("Seattle");
        student4.setState("WA");
        student4.setZipCode("98101");
        student4.setCountry("USA");
        student4.setNotes("Creative problem solver, robotics team lead");

        Student student5 = new Student();
        student5.setName("David Brown");
        student5.setEmail("david.brown@example.com");
        student5.setPhone("+1234567894");
        student5.setDateOfBirth(java.time.LocalDate.of(2000, 5, 18));
        student5.setCourse("Computer Science");
        student5.setMajor("Cybersecurity");
        student5.setSemester("7th");
        student5.setGpa(3.6);
        student5.setCreditsCompleted(105);
        student5.setEnrollmentDate(java.time.LocalDate.of(2021, 9, 1));
        student5.setStatus(Student.StudentStatus.ACTIVE);
        student5.setAddress("654 Maple Dr");
        student5.setCity("Austin");
        student5.setState("TX");
        student5.setZipCode("73301");
        student5.setCountry("USA");
        student5.setNotes("Security enthusiast, ethical hacker certified");

        // Save all students
        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        studentRepository.save(student4);
        studentRepository.save(student5);

        System.out.println("Sample student data initialized successfully!");
    }

    private void initializeSampleCourses() {
        // Create sample courses
        Course course1 = new Course();
        course1.setCourseCode("CS101");
        course1.setCourseName("Introduction to Computer Science");
        course1.setDescription("Fundamental concepts of computer science and programming");
        course1.setCredits(3);
        course1.setDepartment("Computer Science");
        course1.setLevel(Course.CourseLevel.UNDERGRADUATE);
        course1.setInstructor("Dr. Alice Johnson");
        course1.setSchedule("Fall 2024");
        course1.setStartDate(java.time.LocalDateTime.of(2024, 9, 1, 0, 0));
        course1.setEndDate(java.time.LocalDateTime.of(2024, 12, 15, 0, 0));
        course1.setCapacity(30);
        course1.setEnrolledCount(25);
        course1.setStatus(Course.CourseStatus.PUBLISHED);
        course1.setPrerequisites("None");
        course1.setLearningOutcomes("Understand basic programming concepts, problem-solving skills, algorithmic thinking");

        Course course2 = new Course();
        course2.setCourseCode("CS201");
        course2.setCourseName("Data Structures and Algorithms");
        course2.setDescription("Advanced data structures and algorithm analysis");
        course2.setCredits(4);
        course2.setDepartment("Computer Science");
        course2.setLevel(Course.CourseLevel.UNDERGRADUATE);
        course2.setInstructor("Dr. Bob Smith");
        course2.setSchedule("Fall 2024");
        course2.setStartDate(java.time.LocalDateTime.of(2024, 9, 1, 0, 0));
        course2.setEndDate(java.time.LocalDateTime.of(2024, 12, 15, 0, 0));
        course2.setCapacity(25);
        course2.setEnrolledCount(20);
        course2.setStatus(Course.CourseStatus.PUBLISHED);
        course2.setPrerequisites("CS101");
        course2.setLearningOutcomes("Master data structures, analyze algorithm complexity, implement efficient solutions");

        Course course3 = new Course();
        course3.setCourseCode("BUS101");
        course3.setCourseName("Introduction to Business");
        course3.setDescription("Fundamental business concepts and practices");
        course3.setCredits(3);
        course3.setDepartment("Business Administration");
        course3.setLevel(Course.CourseLevel.UNDERGRADUATE);
        course3.setInstructor("Prof. Carol Davis");
        course3.setSchedule("Fall 2024");
        course3.setStartDate(java.time.LocalDateTime.of(2024, 9, 1, 0, 0));
        course3.setEndDate(java.time.LocalDateTime.of(2024, 12, 15, 0, 0));
        course3.setCapacity(40);
        course3.setEnrolledCount(35);
        course3.setStatus(Course.CourseStatus.PUBLISHED);
        course3.setPrerequisites("None");
        course3.setLearningOutcomes("Understand business fundamentals, marketing concepts, financial basics");

        Course course4 = new Course();
        course4.setCourseCode("ENG301");
        course4.setCourseName("Mechanical Engineering Design");
        course4.setDescription("Advanced mechanical design principles and applications");
        course4.setCredits(4);
        course4.setDepartment("Engineering");
        course4.setLevel(Course.CourseLevel.GRADUATE);
        course4.setInstructor("Dr. David Wilson");
        course4.setSchedule("Fall 2024");
        course4.setStartDate(java.time.LocalDateTime.of(2024, 9, 1, 0, 0));
        course4.setEndDate(java.time.LocalDateTime.of(2024, 12, 15, 0, 0));
        course4.setCapacity(20);
        course4.setEnrolledCount(15);
        course4.setStatus(Course.CourseStatus.PUBLISHED);
        course4.setPrerequisites("ENG201, ENG202");
        course4.setLearningOutcomes("Design mechanical systems, apply engineering principles, use CAD software");

        // Save all courses
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);

        System.out.println("Sample course data initialized successfully!");
    }

    private void initializeSampleAssignments() {
        // Get courses for assignment creation
        Course cs101 = courseRepository.findByCourseCode("CS101").orElse(null);
        Course cs201 = courseRepository.findByCourseCode("CS201").orElse(null);
        Course bus101 = courseRepository.findByCourseCode("BUS101").orElse(null);

        if (cs101 != null) {
            Assignment assignment1 = new Assignment();
            assignment1.setTitle("Programming Fundamentals Quiz");
            assignment1.setDescription("Test your knowledge of basic programming concepts");
            assignment1.setCourse(cs101);
            assignment1.setType(Assignment.AssignmentType.QUIZ);
            assignment1.setTotalPoints(100);
            assignment1.setDueDate(java.time.LocalDateTime.of(2024, 10, 15, 23, 59));
            assignment1.setStatus(Assignment.AssignmentStatus.PUBLISHED);
            assignment1.setInstructions("Complete all 20 multiple choice questions");
            assignment1.setTimeLimit(60);
            assignment1.setAllowLateSubmission(true);
            assignment1.setLatePenaltyPercentage(10.0);
            assignment1.setCreatedAt(java.time.LocalDateTime.now());

            Assignment assignment2 = new Assignment();
            assignment2.setTitle("Hello World Program");
            assignment2.setDescription("Write your first program in multiple languages");
            assignment2.setCourse(cs101);
            assignment2.setType(Assignment.AssignmentType.PROJECT);
            assignment2.setTotalPoints(50);
            assignment2.setDueDate(java.time.LocalDateTime.of(2024, 9, 30, 23, 59));
            assignment2.setStatus(Assignment.AssignmentStatus.PUBLISHED);
            assignment2.setInstructions("Submit programs in Python, Java, and C++");
            assignment2.setTimeLimit(null);
            assignment2.setAllowLateSubmission(true);
            assignment2.setLatePenaltyPercentage(15.0);
            assignment2.setCreatedAt(java.time.LocalDateTime.now());

            assignmentRepository.save(assignment1);
            assignmentRepository.save(assignment2);
        }

        if (cs201 != null) {
            Assignment assignment3 = new Assignment();
            assignment3.setTitle("Algorithm Analysis Midterm");
            assignment3.setDescription("Comprehensive midterm exam on algorithms and data structures");
            assignment3.setCourse(cs201);
            assignment3.setType(Assignment.AssignmentType.EXAM);
            assignment3.setTotalPoints(200);
            assignment3.setDueDate(java.time.LocalDateTime.of(2024, 11, 1, 14, 0));
            assignment3.setStatus(Assignment.AssignmentStatus.PUBLISHED);
            assignment3.setInstructions("Complete both theoretical and practical problems");
            assignment3.setTimeLimit(120);
            assignment3.setAllowLateSubmission(false);
            assignment3.setLatePenaltyPercentage(0.0);
            assignment3.setCreatedAt(java.time.LocalDateTime.now());

            assignmentRepository.save(assignment3);
        }

        if (bus101 != null) {
            Assignment assignment4 = new Assignment();
            assignment4.setTitle("Business Plan Project");
            assignment4.setDescription("Create a comprehensive business plan for a startup");
            assignment4.setCourse(bus101);
            assignment4.setType(Assignment.AssignmentType.PROJECT);
            assignment4.setTotalPoints(150);
            assignment4.setDueDate(java.time.LocalDateTime.of(2024, 12, 1, 23, 59));
            assignment4.setStatus(Assignment.AssignmentStatus.PUBLISHED);
            assignment4.setInstructions("Include market analysis, financial projections, and marketing strategy");
            assignment4.setTimeLimit(null);
            assignment4.setAllowLateSubmission(true);
            assignment4.setLatePenaltyPercentage(5.0);
            assignment4.setCreatedAt(java.time.LocalDateTime.now());

            assignmentRepository.save(assignment4);
        }

        System.out.println("Sample assignment data initialized successfully!");
    }

    private void initializeDefaultUser() {
        User defaultUser = new User();
        defaultUser.setUsername("Sudheer");
        defaultUser.setEmail("sudheer@example.com");
        defaultUser.setPassword(passwordEncoder.encode("Sudheer@123"));
        defaultUser.setFirstName("Sudheer");
        defaultUser.setLastName("User");
        defaultUser.setRole(User.UserRole.ADMIN);
        
        userRepository.save(defaultUser);
        
        System.out.println("Default user created successfully!");
        System.out.println("Username: Sudheer");
        System.out.println("Password: Sudheer@123");
        System.out.println("Role: ADMIN");
    }
}
