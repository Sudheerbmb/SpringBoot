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

  private api = 'http://localhost:8080/students';

  constructor(private http: HttpClient) {}

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.api);
  }

  addStudent(student: Student): Observable<Student> {
    return this.http.post<Student>(this.api, student);
  }

  deleteStudent(id: number) {
    return this.http.delete(`${this.api}/${id}`);
  }
}