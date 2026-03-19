import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of, tap, catchError, shareReplay, finalize } from 'rxjs';

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
  status: string;
  capacity: number;
  enrolledCount: number;
  instructor: string;
  schedule: string;
  startDate: string;
  endDate: string;
  prerequisites: string;
  learningOutcomes: string;
  createdAt: string;
  updatedAt: string;
}

export interface Assignment {
  id: number;
  title: string;
  description: string;
  type: string;
  totalPoints: number;
  dueDate: string;
  status: string;
  instructions: string;
  timeLimit: number | null;
  allowLateSubmission: boolean;
  latePenaltyPercentage: number | null;
  createdAt: string;
  updatedAt: string;
  courseId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'https://springboot-1-1stn.onrender.com/api';
  
  // Cache with BehaviorSubjects for instant updates
  private studentsCache = new BehaviorSubject<Student[]>([]);
  private coursesCache = new BehaviorSubject<Course[]>([]);
  private assignmentsCache = new BehaviorSubject<Assignment[]>([]);
  
  // Loading states
  private studentsLoading = new BehaviorSubject<boolean>(false);
  private coursesLoading = new BehaviorSubject<boolean>(false);
  private assignmentsLoading = new BehaviorSubject<boolean>(false);
  
  // Expose observables
  public students$ = this.studentsCache.asObservable();
  public courses$ = this.coursesCache.asObservable();
  public assignments$ = this.assignmentsCache.asObservable();
  public studentsLoading$ = this.studentsLoading.asObservable();
  public coursesLoading$ = this.coursesLoading.asObservable();
  public assignmentsLoading$ = this.assignmentsLoading.asObservable();

  constructor(private http: HttpClient) {
    // Preload data on service init
    this.preloadData();
  }

  private preloadData(): void {
    this.loadStudents();
    this.loadCourses();
    this.loadAssignments();
  }

  // Student endpoints with caching
  loadStudents(): void {
    if (this.studentsLoading.value) return;
    this.studentsLoading.next(true);
    
    this.http.get<Student[]>(`${this.baseUrl}/students`).pipe(
      tap(students => this.studentsCache.next(students)),
      catchError(() => of([])),
      finalize(() => this.studentsLoading.next(false))
    ).subscribe();
  }

  getStudents(): Observable<Student[]> {
    if (this.studentsCache.value.length === 0) {
      this.loadStudents();
    }
    return this.students$;
  }

  getStudent(id: number): Observable<Student> {
    const cached = this.studentsCache.value.find(s => s.id === id);
    if (cached) return of(cached);
    return this.http.get<Student>(`${this.baseUrl}/students/${id}`);
  }

  createStudent(student: Partial<Student>): Observable<Student> {
    return this.http.post<Student>(`${this.baseUrl}/students`, student).pipe(
      tap(newStudent => {
        const current = this.studentsCache.value;
        this.studentsCache.next([...current, newStudent]);
      })
    );
  }

  updateStudent(id: number, student: Partial<Student>): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/students/${id}`, student).pipe(
      tap(updated => {
        const current = this.studentsCache.value;
        const index = current.findIndex(s => s.id === id);
        if (index !== -1) {
          current[index] = { ...current[index], ...updated };
          this.studentsCache.next([...current]);
        }
      })
    );
  }

  deleteStudent(id: number): Observable<void> {
    // Optimistic delete - remove immediately
    const current = this.studentsCache.value;
    const filtered = current.filter(s => s.id !== id);
    this.studentsCache.next(filtered);
    
    return this.http.delete<void>(`${this.baseUrl}/students/${id}`).pipe(
      catchError(err => {
        // Restore on error
        this.studentsCache.next(current);
        throw err;
      })
    );
  }

  // Course endpoints with caching
  loadCourses(): void {
    if (this.coursesLoading.value) return;
    this.coursesLoading.next(true);
    
    this.http.get<Course[]>(`${this.baseUrl}/courses`).pipe(
      tap(courses => this.coursesCache.next(courses)),
      catchError(() => of([])),
      finalize(() => this.coursesLoading.next(false))
    ).subscribe();
  }

  getCourses(): Observable<Course[]> {
    if (this.coursesCache.value.length === 0) {
      this.loadCourses();
    }
    return this.courses$;
  }

  getCourse(id: number): Observable<Course> {
    const cached = this.coursesCache.value.find(c => c.id === id);
    if (cached) return of(cached);
    return this.http.get<Course>(`${this.baseUrl}/courses/${id}`);
  }

  createCourse(course: Partial<Course>): Observable<Course> {
    return this.http.post<Course>(`${this.baseUrl}/courses`, course).pipe(
      tap(newCourse => {
        const current = this.coursesCache.value;
        this.coursesCache.next([...current, newCourse]);
      })
    );
  }

  updateCourse(id: number, course: Partial<Course>): Observable<Course> {
    return this.http.put<Course>(`${this.baseUrl}/courses/${id}`, course).pipe(
      tap(updated => {
        const current = this.coursesCache.value;
        const index = current.findIndex(c => c.id === id);
        if (index !== -1) {
          current[index] = { ...current[index], ...updated };
          this.coursesCache.next([...current]);
        }
      })
    );
  }

  deleteCourse(id: number): Observable<void> {
    // Optimistic delete
    const current = this.coursesCache.value;
    const filtered = current.filter(c => c.id !== id);
    this.coursesCache.next(filtered);
    
    return this.http.delete<void>(`${this.baseUrl}/courses/${id}`).pipe(
      catchError(err => {
        this.coursesCache.next(current);
        throw err;
      })
    );
  }

  // Assignment endpoints with caching
  loadAssignments(): void {
    if (this.assignmentsLoading.value) return;
    this.assignmentsLoading.next(true);
    
    this.http.get<Assignment[]>(`${this.baseUrl}/assignments`).pipe(
      tap(assignments => this.assignmentsCache.next(assignments)),
      catchError(() => of([])),
      finalize(() => this.assignmentsLoading.next(false))
    ).subscribe();
  }

  getAssignments(): Observable<Assignment[]> {
    if (this.assignmentsCache.value.length === 0) {
      this.loadAssignments();
    }
    return this.assignments$;
  }

  getAssignment(id: number): Observable<Assignment> {
    const cached = this.assignmentsCache.value.find(a => a.id === id);
    if (cached) return of(cached);
    return this.http.get<Assignment>(`${this.baseUrl}/assignments/${id}`);
  }

  createAssignment(assignment: Partial<Assignment>): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.baseUrl}/assignments`, assignment).pipe(
      tap(newAssignment => {
        const current = this.assignmentsCache.value;
        this.assignmentsCache.next([...current, newAssignment]);
      })
    );
  }

  updateAssignment(id: number, assignment: Partial<Assignment>): Observable<Assignment> {
    return this.http.put<Assignment>(`${this.baseUrl}/assignments/${id}`, assignment).pipe(
      tap(updated => {
        const current = this.assignmentsCache.value;
        const index = current.findIndex(a => a.id === id);
        if (index !== -1) {
          current[index] = { ...current[index], ...updated };
          this.assignmentsCache.next([...current]);
        }
      })
    );
  }

  deleteAssignment(id: number): Observable<void> {
    // Optimistic delete
    const current = this.assignmentsCache.value;
    const filtered = current.filter(a => a.id !== id);
    this.assignmentsCache.next(filtered);
    
    return this.http.delete<void>(`${this.baseUrl}/assignments/${id}`).pipe(
      catchError(err => {
        this.assignmentsCache.next(current);
        throw err;
      })
    );
  }

  // Refresh methods
  refreshStudents(): void {
    this.loadStudents();
  }

  refreshCourses(): void {
    this.loadCourses();
  }

  refreshAssignments(): void {
    this.loadAssignments();
  }
}
