import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Student {
  id: number;
  name: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  course: string;
  major: string;
  semester: string;
  gpa: number;
  creditsCompleted: number;
  enrollmentDate: string;
  graduationDate?: string;
  status: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  notes: string;
  createdAt: string;
  updatedAt: string;
}

export interface Course {
  id: number;
  courseCode: string;
  courseName: string;
  description: string;
  credits: number;
  department: string;
  level: string;
  instructor: string;
  semester: string;
  academicYear: string;
  startDate: string;
  endDate: string;
  maxStudents: number;
  currentEnrollment: number;
  status: string;
  prerequisites: string;
  learningObjectives: string;
  createdAt: string;
  updatedAt: string;
}

export interface Assignment {
  id: number;
  title: string;
  description: string;
  course: Course;
  type: string;
  totalPoints: number;
  dueDate: string;
  assignedDate: string;
  status: string;
  instructions: string;
  attachments: string;
  timeLimit: number;
  allowsLateSubmission: boolean;
  latePenalty: number;
  publishedDate: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'https://springboot-1-1stn.onrender.com/api';

  constructor(private http: HttpClient) {}

  // Student endpoints
  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/students`);
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/students/${id}`);
  }

  createStudent(student: Partial<Student>): Observable<Student> {
    return this.http.post<Student>(`${this.baseUrl}/students`, student);
  }

  updateStudent(id: number, student: Partial<Student>): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/students/${id}`, student);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/students/${id}`);
  }

  searchStudents(keyword: string): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/students/search?keyword=${keyword}`);
  }

  getStudentsByCourse(course: string): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/students/course/${course}`);
  }

  getStudentsByStatus(status: string): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/students/status/${status}`);
  }

  // Course endpoints
  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses`);
  }

  getCourse(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.baseUrl}/courses/${id}`);
  }

  createCourse(course: Partial<Course>): Observable<Course> {
    return this.http.post<Course>(`${this.baseUrl}/courses`, course);
  }

  updateCourse(id: number, course: Partial<Course>): Observable<Course> {
    return this.http.put<Course>(`${this.baseUrl}/courses/${id}`, course);
  }

  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/courses/${id}`);
  }

  getCoursesByDepartment(department: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses/department/${department}`);
  }

  getCoursesByInstructor(instructor: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses/instructor/${instructor}`);
  }

  // Assignment endpoints
  getAssignments(): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.baseUrl}/assignments`);
  }

  getAssignment(id: number): Observable<Assignment> {
    return this.http.get<Assignment>(`${this.baseUrl}/assignments/${id}`);
  }

  createAssignment(assignment: Partial<Assignment>): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.baseUrl}/assignments`, assignment);
  }

  updateAssignment(id: number, assignment: Partial<Assignment>): Observable<Assignment> {
    return this.http.put<Assignment>(`${this.baseUrl}/assignments/${id}`, assignment);
  }

  deleteAssignment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/assignments/${id}`);
  }

  getAssignmentsByCourse(courseId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.baseUrl}/assignments/course/${courseId}`);
  }

  getAssignmentsByStudent(studentId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.baseUrl}/assignments/student/${studentId}`);
  }
}
