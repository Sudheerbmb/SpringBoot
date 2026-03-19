import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Student } from '../services/api.service';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="students-container">
      <div class="students-header">
        <h2>👥 Students Management</h2>
        <div class="header-actions">
          <button (click)="showAddForm = !showAddForm" class="add-btn">
            {{ showAddForm ? 'Cancel' : '+ Add Student' }}
          </button>
          <button (click)="loadStudents()" class="refresh-btn">🔄 Refresh</button>
        </div>
      </div>

      <!-- Add Student Form -->
      <div *ngIf="showAddForm" class="add-student-form">
        <h3>Add New Student</h3>
        <form [formGroup]="studentForm" (ngSubmit)="addStudent()">
          <div class="form-grid">
            <div class="form-group">
              <label for="name">Name*</label>
              <input id="name" type="text" formControlName="name" placeholder="Enter student name">
              <div class="error" *ngIf="studentForm.get('name')?.hasError('required')">
                Name is required
              </div>
            </div>

            <div class="form-group">
              <label for="email">Email*</label>
              <input id="email" type="email" formControlName="email" placeholder="Enter email">
              <div class="error" *ngIf="studentForm.get('email')?.hasError('required')">
                Email is required
              </div>
              <div class="error" *ngIf="studentForm.get('email')?.hasError('email')">
                Please enter a valid email
              </div>
            </div>

            <div class="form-group">
              <label for="phone">Phone</label>
              <input id="phone" type="tel" formControlName="phone" placeholder="Enter phone number">
            </div>

            <div class="form-group">
              <label for="course">Course*</label>
              <input id="course" type="text" formControlName="course" placeholder="Enter course">
              <div class="error" *ngIf="studentForm.get('course')?.hasError('required')">
                Course is required
              </div>
            </div>

            <div class="form-group">
              <label for="major">Major</label>
              <input id="major" type="text" formControlName="major" placeholder="Enter major">
            </div>

            <div class="form-group">
              <label for="semester">Semester</label>
              <input id="semester" type="text" formControlName="semester" placeholder="Enter semester">
            </div>

            <div class="form-group">
              <label for="gpa">GPA</label>
              <input id="gpa" type="number" step="0.1" min="0" max="4" formControlName="gpa" placeholder="Enter GPA">
            </div>

            <div class="form-group">
              <label for="creditsCompleted">Credits Completed</label>
              <input id="creditsCompleted" type="number" min="0" formControlName="creditsCompleted" placeholder="Enter credits">
            </div>

            <div class="form-group">
              <label for="status">Status</label>
              <select id="status" formControlName="status">
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
                <option value="GRADUATED">Graduated</option>
                <option value="SUSPENDED">Suspended</option>
                <option value="ON_LEAVE">On Leave</option>
              </select>
            </div>

            <div class="form-group full-width">
              <label for="address">Address</label>
              <input id="address" type="text" formControlName="address" placeholder="Enter address">
            </div>

            <div class="form-group">
              <label for="city">City</label>
              <input id="city" type="text" formControlName="city" placeholder="Enter city">
            </div>

            <div class="form-group">
              <label for="state">State</label>
              <input id="state" type="text" formControlName="state" placeholder="Enter state">
            </div>

            <div class="form-group">
              <label for="zipCode">Zip Code</label>
              <input id="zipCode" type="text" formControlName="zipCode" placeholder="Enter zip code">
            </div>

            <div class="form-group">
              <label for="country">Country</label>
              <input id="country" type="text" formControlName="country" placeholder="Enter country">
            </div>

            <div class="form-group full-width">
              <label for="notes">Notes</label>
              <textarea id="notes" formControlName="notes" rows="3" placeholder="Enter notes"></textarea>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" [disabled]="studentForm.invalid || isSubmitting" class="submit-btn">
              {{ isSubmitting ? 'Adding...' : 'Add Student' }}
            </button>
            <button type="button" (click)="showAddForm = false" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>

      <!-- Students List -->
      <div class="students-content">
        <div *ngIf="isLoading" class="loading">
          <div class="spinner"></div>
          <p>Loading students...</p>
        </div>
        
        <div *ngIf="error" class="error">
          <p>{{ error }}</p>
        </div>
        
        <div *ngIf="!isLoading && !error" class="students-grid">
          <div *ngFor="let student of students" class="student-card">
            <div class="student-header">
              <h3>{{ student.name }}</h3>
              <span class="status-badge" [class]="student.status.toLowerCase()">
                {{ student.status }}
              </span>
            </div>
            
            <div class="student-info">
              <p><strong>Email:</strong> {{ student.email }}</p>
              <p><strong>Phone:</strong> {{ student.phone || 'N/A' }}</p>
              <p><strong>Course:</strong> {{ student.course }}</p>
              <p><strong>Major:</strong> {{ student.major || 'N/A' }}</p>
              <p><strong>Semester:</strong> {{ student.semester || 'N/A' }}</p>
              <p><strong>GPA:</strong> <span class="gpa-badge" [class]="getGpaClass(student.gpa)">{{ student.gpa || 'N/A' }}</span></p>
              <p><strong>Credits:</strong> {{ student.creditsCompleted || 0 }}</p>
              <p><strong>Enrollment:</strong> {{ formatDate(student.enrollmentDate) }}</p>
              
              <div *ngIf="student.address" class="address-info">
                <p><strong>Address:</strong> {{ student.address }}, {{ student.city || '' }}, {{ student.state || '' }}</p>
              </div>
              
              <div *ngIf="student.notes" class="notes-info">
                <p><strong>Notes:</strong> {{ student.notes }}</p>
              </div>
            </div>
            
            <div class="student-actions">
              <button (click)="editStudent(student)" class="edit-btn">✏️ Edit</button>
              <button (click)="deleteStudent(student.id)" class="delete-btn">🗑️ Delete</button>
            </div>
          </div>
        </div>
        
        <div *ngIf="!isLoading && !error && students.length === 0" class="no-students">
          <p>No students found. Start by adding some students!</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .students-container {
      padding: 20px;
      max-width: 1400px;
      margin: 0 auto;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .students-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      background: white;
      padding: 20px;
      border-radius: 15px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .students-header h2 {
      color: #333;
      font-size: 2rem;
      margin: 0;
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }

    .add-btn, .refresh-btn {
      background: #28a745;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 8px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s;
    }

    .refresh-btn {
      background: #17a2b8;
    }

    .add-btn:hover, .refresh-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    }

    .add-student-form {
      background: white;
      border-radius: 15px;
      padding: 30px;
      margin-bottom: 30px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }

    .add-student-form h3 {
      color: #333;
      margin-bottom: 25px;
      text-align: center;
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
    }

    .form-group.full-width {
      grid-column: 1 / -1;
    }

    .form-group label {
      color: #333;
      font-weight: 500;
      margin-bottom: 5px;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      padding: 12px;
      border: 2px solid #e9ecef;
      border-radius: 8px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    .form-group input:focus,
    .form-group select:focus,
    .form-group textarea:focus {
      outline: none;
      border-color: #667eea;
    }

    .error {
      color: #dc3545;
      font-size: 12px;
      margin-top: 5px;
    }

    .form-actions {
      display: flex;
      gap: 10px;
      justify-content: center;
    }

    .submit-btn, .cancel-btn {
      padding: 12px 30px;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s;
    }

    .submit-btn {
      background: #28a745;
      color: white;
    }

    .cancel-btn {
      background: #6c757d;
      color: white;
    }

    .submit-btn:hover, .cancel-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    }

    .loading, .error, .no-students {
      text-align: center;
      padding: 60px 20px;
      background: white;
      border-radius: 15px;
      margin-bottom: 30px;
    }

    .loading {
      color: #667eea;
    }

    .spinner {
      border: 4px solid #f3f3f3;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      animation: spin 1s linear infinite;
      margin: 0 auto 20px;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .error {
      color: #dc3545;
      background: #f8d7da;
      border: 1px solid #f5c6cb;
    }

    .no-students {
      color: #6c757d;
      background: #f8f9fa;
    }

    .students-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 25px;
    }

    .student-card {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
      border-left: 5px solid #667eea;
      transition: all 0.3s;
    }

    .student-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 30px rgba(0,0,0,0.15);
    }

    .student-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
    }

    .student-header h3 {
      color: #333;
      margin: 0;
      font-size: 1.3rem;
    }

    .status-badge {
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
      text-transform: uppercase;
    }

    .status-badge.active {
      background: #d4edda;
      color: #155724;
    }

    .status-badge.inactive {
      background: #f8d7da;
      color: #721c24;
    }

    .status-badge.graduated {
      background: #d1ecf1;
      color: #0c5460;
    }

    .status-badge.suspended {
      background: #fff3cd;
      color: #856404;
    }

    .status-badge.on_leave {
      background: #e2e3e5;
      color: #383d41;
    }

    .student-info p {
      color: #666;
      margin-bottom: 8px;
      font-size: 0.9rem;
    }

    .student-info strong {
      color: #333;
    }

    .gpa-badge {
      padding: 2px 8px;
      border-radius: 12px;
      font-weight: 500;
      font-size: 0.85rem;
    }

    .gpa-badge.excellent {
      background: #d4edda;
      color: #155724;
    }

    .gpa-badge.good {
      background: #d1ecf1;
      color: #0c5460;
    }

    .gpa-badge.average {
      background: #fff3cd;
      color: #856404;
    }

    .gpa-badge.poor {
      background: #f8d7da;
      color: #721c24;
    }

    .address-info, .notes-info {
      margin-top: 10px;
      padding-top: 10px;
      border-top: 1px solid #e9ecef;
    }

    .student-actions {
      display: flex;
      gap: 10px;
      margin-top: 20px;
    }

    .edit-btn, .delete-btn {
      flex: 1;
      padding: 8px 15px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s;
    }

    .edit-btn {
      background: #ffc107;
      color: #212529;
    }

    .delete-btn {
      background: #dc3545;
      color: white;
    }

    .edit-btn:hover, .delete-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 3px 10px rgba(0,0,0,0.2);
    }
  `]
})
export class StudentsComponent implements OnInit {
  students: Student[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  isSubmitting = false;
  studentForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.studentForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      course: ['', Validators.required],
      major: [''],
      semester: [''],
      gpa: [null],
      creditsCompleted: [null],
      status: ['ACTIVE'],
      address: [''],
      city: [''],
      state: [''],
      zipCode: [''],
      country: [''],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.isLoading = true;
    this.error = null;
    
    this.apiService.getStudents().subscribe({
      next: (data) => {
        this.students = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load students. Please try again.';
        this.isLoading = false;
        console.error('Error loading students:', err);
      }
    });
  }

  addStudent(): void {
    if (this.studentForm.valid) {
      this.isSubmitting = true;
      
      const studentData = {
        ...this.studentForm.value,
        dateOfBirth: '2000-01-01', // Default date
        enrollmentDate: new Date().toISOString(),
        graduationDate: null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };

      this.apiService.createStudent(studentData).subscribe({
        next: (newStudent) => {
          this.students.push(newStudent);
          this.showAddForm = false;
          this.studentForm.reset();
          this.isSubmitting = false;
        },
        error: (err) => {
          alert('Failed to add student. Please try again.');
          this.isSubmitting = false;
          console.error('Error adding student:', err);
        }
      });
    }
  }

  editStudent(student: Student): void {
    alert('Edit functionality coming soon! 📝\n\nStudent: ' + student.name);
  }

  deleteStudent(id: number): void {
    if (confirm('Are you sure you want to delete this student?')) {
      this.apiService.deleteStudent(id).subscribe({
        next: () => {
          this.students = this.students.filter(s => s.id !== id);
        },
        error: (err) => {
          alert('Failed to delete student. Please try again.');
          console.error('Error deleting student:', err);
        }
      });
    }
  }

  getGpaClass(gpa: number | null | undefined): string {
    if (!gpa) return '';
    if (gpa >= 3.7) return 'excellent';
    if (gpa >= 3.3) return 'good';
    if (gpa >= 2.7) return 'average';
    return 'poor';
  }

  formatDate(dateString: string | null | undefined): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}
