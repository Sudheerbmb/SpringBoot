import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, throwError } from 'rxjs';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  user?: User;
  error?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://springboot-1-1stn.onrender.com/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const userData = localStorage.getItem('user');
    if (userData) {
      try {
        this.currentUserSubject.next(JSON.parse(userData));
      } catch (e) {
        localStorage.removeItem('user');
      }
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.success && response.user) {
          localStorage.setItem('user', JSON.stringify(response.user));
          this.currentUserSubject.next(response.user);
        }
      }),
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      catchError(error => {
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const currentUser = this.currentUserSubject.value;
    return currentUser?.role === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isTeacher(): boolean {
    return this.hasRole('TEACHER') || this.isAdmin();
  }
}
