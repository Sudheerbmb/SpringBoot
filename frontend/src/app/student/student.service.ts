import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Student {
  id?: number;
  name: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class StudentService {

  private api = 'https://springboot-1-1stn.onrender.com/students';

  constructor(private http: HttpClient) {}

  // ✅ GET ALL
  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.api);
  }

  // ✅ GET BY ID
  getStudentById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.api}/${id}`);
  }

  // ✅ CREATE
  addStudent(student: Student): Observable<Student> {
    return this.http.post<Student>(this.api, student);
  }

  // ✅ UPDATE
  updateStudent(id: number, student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.api}/${id}`, student);
  }

  // ✅ DELETE
  deleteStudent(id: number): Observable<any> {
    return this.http.delete(`${this.api}/${id}`);
  }
}