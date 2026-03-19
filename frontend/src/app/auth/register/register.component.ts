import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ],
  template: `
    <div class="register-container">
      <div class="register-card">
        <h2>Create Account</h2>
        <p>Join our Learning Management System</p>
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="username">Username</label>
            <input id="username" type="text" formControlName="username" placeholder="Choose a username">
            <div class="error" *ngIf="registerForm.get('username')?.hasError('required')">
              Username is required
            </div>
            <div class="error" *ngIf="registerForm.get('username')?.hasError('minlength')">
              Username must be at least 3 characters
            </div>
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" formControlName="email" placeholder="Enter your email">
            <div class="error" *ngIf="registerForm.get('email')?.hasError('required')">
              Email is required
            </div>
            <div class="error" *ngIf="registerForm.get('email')?.hasError('email')">
              Please enter a valid email
            </div>
          </div>

          <div class="form-group">
            <label for="firstName">First Name</label>
            <input id="firstName" type="text" formControlName="firstName" placeholder="Enter your first name">
            <div class="error" *ngIf="registerForm.get('firstName')?.hasError('required')">
              First name is required
            </div>
          </div>

          <div class="form-group">
            <label for="lastName">Last Name</label>
            <input id="lastName" type="text" formControlName="lastName" placeholder="Enter your last name">
            <div class="error" *ngIf="registerForm.get('lastName')?.hasError('required')">
              Last name is required
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <input id="password" type="password" formControlName="password" placeholder="Create a password">
            <div class="error" *ngIf="registerForm.get('password')?.hasError('required')">
              Password is required
            </div>
            <div class="error" *ngIf="registerForm.get('password')?.hasError('minlength')">
              Password must be at least 6 characters
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input id="confirmPassword" type="password" formControlName="confirmPassword" placeholder="Confirm your password">
            <div class="error" *ngIf="registerForm.get('confirmPassword')?.hasError('required')">
              Please confirm your password
            </div>
            <div class="error" *ngIf="registerForm.errors?.['passwordMismatch']">
              Passwords do not match
            </div>
          </div>

          <div class="form-group">
            <label for="role">Role</label>
            <select id="role" formControlName="role">
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <button type="submit" [disabled]="registerForm.invalid || isLoading" class="register-btn">
            {{ isLoading ? 'Creating account...' : 'Create Account' }}
          </button>
        </form>
        <div class="login-link">
          Already have an account? <a routerLink="/login">Sign in here</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      margin: 0;
      font-family: Arial, sans-serif;
    }

    .register-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.2);
      max-width: 450px;
      width: 90%;
    }

    h2 {
      text-align: center;
      color: #333;
      margin-bottom: 10px;
      font-size: 24px;
      font-weight: 600;
    }

    p {
      text-align: center;
      color: #666;
      margin-bottom: 30px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      color: #333;
      font-weight: 500;
    }

    input, select {
      width: 100%;
      padding: 12px;
      border: 2px solid #ddd;
      border-radius: 5px;
      font-size: 16px;
      box-sizing: border-box;
    }

    input:focus, select:focus {
      outline: none;
      border-color: #667eea;
    }

    .error {
      color: #e74c3c;
      font-size: 14px;
      margin-top: 5px;
    }

    .register-btn {
      width: 100%;
      padding: 12px;
      background: #667eea;
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.3s;
    }

    .register-btn:hover:not(:disabled) {
      background: #5a6fd8;
    }

    .register-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
    }

    .login-link {
      text-align: center;
      margin-top: 20px;
      color: #666;
    }

    .login-link a {
      color: #667eea;
      text-decoration: none;
      font-weight: 500;
    }

    .login-link a:hover {
      text-decoration: underline;
    }
  `]
})
export class RegisterComponent implements OnInit {
  registerForm: any;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: ['STUDENT', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(form: any) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      
      const registerData = {
        username: this.registerForm.value.username,
        email: this.registerForm.value.email,
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
        password: this.registerForm.value.password,
        role: this.registerForm.value.role
      };

      console.log('Registering user:', registerData);

      this.authService.register(registerData).subscribe({
        next: (response) => {
          console.log('Registration successful:', response);
          alert('Registration successful! Please login.');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Registration error details:', error);
          this.isLoading = false;
          if (error.status === 0) {
            alert('Cannot connect to backend. Please check if the server is running.');
          } else {
            alert('Registration failed: ' + (error.error?.message || error.message || 'Unknown error'));
          }
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    }
  }
}
