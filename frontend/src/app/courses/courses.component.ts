import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Course } from '../services/api.service';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="courses-container">
      <div class="courses-header">
        <h2>📚 Courses Management</h2>
        <div class="header-actions">
          <button (click)="showAddForm = !showAddForm" class="add-btn">
            {{ showAddForm ? 'Cancel' : '+ Add Course' }}
          </button>
          <button (click)="loadCourses()" class="refresh-btn">🔄 Refresh</button>
        </div>
      </div>

      <!-- Add Course Form -->
      <div *ngIf="showAddForm" class="add-course-form">
        <h3>Add New Course</h3>
        <form [formGroup]="courseForm" (ngSubmit)="addCourse()">
          <div class="form-grid">
            <div class="form-group">
              <label for="courseCode">Course Code*</label>
              <input id="courseCode" type="text" formControlName="courseCode" placeholder="e.g., CS101">
              <div class="error" *ngIf="courseForm.get('courseCode')?.hasError('required')">
                Course code is required
              </div>
            </div>

            <div class="form-group">
              <label for="courseName">Course Name*</label>
              <input id="courseName" type="text" formControlName="courseName" placeholder="e.g., Introduction to Computer Science">
              <div class="error" *ngIf="courseForm.get('courseName')?.hasError('required')">
                Course name is required
              </div>
            </div>

            <div class="form-group">
              <label for="department">Department*</label>
              <input id="department" type="text" formControlName="department" placeholder="e.g., Computer Science">
              <div class="error" *ngIf="courseForm.get('department')?.hasError('required')">
                Department is required
              </div>
            </div>

            <div class="form-group">
              <label for="instructor">Instructor*</label>
              <input id="instructor" type="text" formControlName="instructor" placeholder="e.g., Dr. Smith">
              <div class="error" *ngIf="courseForm.get('instructor')?.hasError('required')">
                Instructor is required
              </div>
            </div>

            <div class="form-group">
              <label for="credits">Credits*</label>
              <input id="credits" type="number" formControlName="credits" placeholder="e.g., 3">
              <div class="error" *ngIf="courseForm.get('credits')?.hasError('required')">
                Credits are required
              </div>
            </div>

            <div class="form-group">
              <label for="level">Level*</label>
              <select id="level" formControlName="level">
                <option value="">Select Level</option>
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
                <option value="GRADUATE">Graduate</option>
              </select>
              <div class="error" *ngIf="courseForm.get('level')?.hasError('required')">
                Level is required
              </div>
            </div>

            <div class="form-group">
              <label for="schedule">Schedule*</label>
              <input id="schedule" type="text" formControlName="schedule" placeholder="e.g., Mon/Wed 10:00-11:30 AM">
              <div class="error" *ngIf="courseForm.get('schedule')?.hasError('required')">
                Schedule is required
              </div>
            </div>

            <div class="form-group">
              <label for="capacity">Capacity*</label>
              <input id="capacity" type="number" formControlName="capacity" placeholder="e.g., 30">
              <div class="error" *ngIf="courseForm.get('capacity')?.hasError('required')">
                Capacity is required
              </div>
            </div>

            <div class="form-group full-width">
              <label for="description">Description</label>
              <textarea id="description" formControlName="description" rows="3" placeholder="Course description..."></textarea>
            </div>

            <div class="form-group full-width">
              <label for="prerequisites">Prerequisites</label>
              <input id="prerequisites" type="text" formControlName="prerequisites" placeholder="e.g., CS100, MATH101">
            </div>

            <div class="form-group full-width">
              <label for="learningOutcomes">Learning Outcomes</label>
              <textarea id="learningOutcomes" formControlName="learningOutcomes" rows="2" placeholder="What students will learn..."></textarea>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" [disabled]="courseForm.invalid || isSubmitting" class="submit-btn">
              {{ isSubmitting ? 'Adding...' : 'Add Course' }}
            </button>
            <button type="button" (click)="showAddForm = false" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>

      <!-- Courses List -->
      <div class="courses-content">
        <div *ngIf="isLoading" class="loading">
          <div class="spinner"></div>
          <p>Loading courses...</p>
        </div>
        
        <div *ngIf="error" class="error">
          <p>{{ error }}</p>
        </div>
        
        <div *ngIf="!isLoading && !error" class="courses-grid">
          <div *ngFor="let course of courses" class="course-card">
            <div class="course-header">
              <h3>{{ course.courseCode }}</h3>
              <span class="status-badge" [class]="course.status.toLowerCase()">
                {{ course.status }}
              </span>
            </div>
            
            <div class="course-info">
              <h4>{{ course.courseName }}</h4>
              <p><strong>Department:</strong> {{ course.department }}</p>
              <p><strong>Instructor:</strong> {{ course.instructor }}</p>
              <p><strong>Credits:</strong> {{ course.credits }}</p>
              <p><strong>Level:</strong> {{ course.level }}</p>
              <p><strong>Schedule:</strong> {{ course.schedule }}</p>
              <p><strong>Enrollment:</strong> {{ course.enrolledCount || 0 }}/{{ course.capacity }}</p>
              
              <div class="progress-bar">
                <div class="progress-fill" [style.width.%]="getEnrollmentPercentage(course)"></div>
              </div>
              
              <div *ngIf="course.description" class="description">
                <p>{{ course.description }}</p>
              </div>
            </div>
            
            <div class="course-actions">
              <button (click)="editCourse(course)" class="edit-btn">✏️ Edit</button>
              <button (click)="deleteCourse(course.id)" class="delete-btn">🗑️ Delete</button>
            </div>
          </div>
        </div>
        
        <div *ngIf="!isLoading && !error && courses.length === 0" class="no-courses">
          <p>No courses found. Start by adding some courses!</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .courses-container {
      padding: 20px;
      max-width: 1400px;
      margin: 0 auto;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .courses-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      background: white;
      padding: 20px;
      border-radius: 15px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .courses-header h2 {
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

    .add-course-form {
      background: white;
      border-radius: 15px;
      padding: 30px;
      margin-bottom: 30px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }

    .add-course-form h3 {
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

    .loading, .error, .no-courses {
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

    .no-courses {
      color: #6c757d;
      background: #f8f9fa;
    }

    .courses-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 25px;
    }

    .course-card {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
      border-left: 5px solid #667eea;
      transition: all 0.3s;
    }

    .course-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 30px rgba(0,0,0,0.15);
    }

    .course-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
    }

    .course-header h3 {
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

    .status-badge.completed {
      background: #d1ecf1;
      color: #0c5460;
    }

    .course-info h4 {
      color: #333;
      margin-bottom: 15px;
      font-size: 1.1rem;
    }

    .course-info p {
      color: #666;
      margin-bottom: 8px;
      font-size: 0.9rem;
    }

    .course-info strong {
      color: #333;
    }

    .progress-bar {
      width: 100%;
      height: 8px;
      background: #e9ecef;
      border-radius: 4px;
      margin: 10px 0;
      overflow: hidden;
    }

    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #28a745, #20c997);
      transition: width 0.3s;
    }

    .description {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #e9ecef;
    }

    .description p {
      font-style: italic;
      line-height: 1.5;
    }

    .course-actions {
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
export class CoursesComponent implements OnInit {
  courses: Course[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  isSubmitting = false;
  courseForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.courseForm = this.fb.group({
      courseCode: ['', Validators.required],
      courseName: ['', Validators.required],
      description: [''],
      credits: [0, Validators.required],
      department: ['', Validators.required],
      level: ['', Validators.required],
      instructor: ['', Validators.required],
      schedule: ['', Validators.required],
      capacity: [0, Validators.required],
      prerequisites: [''],
      learningOutcomes: ['']
    });
  }

  ngOnInit(): void {
    // Subscribe to cached data for instant loading
    this.apiService.courses$.subscribe(courses => {
      this.courses = courses;
    });
    this.apiService.coursesLoading$.subscribe(loading => {
      this.isLoading = loading;
    });
  }

  loadCourses(): void {
    this.apiService.refreshCourses();
  }

  addCourse(): void {
    if (this.courseForm.valid) {
      this.isSubmitting = true;
      
      const courseData = {
        ...this.courseForm.value,
        status: 'DRAFT',
        enrolledCount: 0,
        startDate: new Date().toISOString(),
        endDate: new Date().toISOString()
      };

      this.apiService.createCourse(courseData).subscribe({
        next: (newCourse) => {
          this.courses.push(newCourse);
          this.showAddForm = false;
          this.courseForm.reset();
          this.isSubmitting = false;
        },
        error: (err) => {
          alert('Failed to add course. Please try again.');
          this.isSubmitting = false;
          console.error('Error adding course:', err);
        }
      });
    }
  }

  editCourse(course: Course): void {
    alert('Edit functionality coming soon! 📝');
  }

  deleteCourse(id: number): void {
    if (confirm('Are you sure you want to delete this course?')) {
      // Optimistic delete - UI updates immediately via service
      this.apiService.deleteCourse(id).subscribe({
        error: (err) => {
          alert('Failed to delete course. Please try again.');
          console.error('Error deleting course:', err);
        }
      });
    }
  }

  getEnrollmentPercentage(course: Course): number {
    const max = course.capacity || 1;
    const current = course.enrolledCount || 0;
    return Math.min((current / max) * 100, 100);
  }
}
