import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Assignment } from '../services/api.service';

@Component({
  selector: 'app-assignments',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="assignments-container">
      <div class="assignments-header">
        <h2>📝 Assignments Management</h2>
        <div class="header-actions">
          <button (click)="showAddForm = !showAddForm" class="add-btn">
            {{ showAddForm ? 'Cancel' : '+ Add Assignment' }}
          </button>
          <button (click)="loadAssignments()" class="refresh-btn">🔄 Refresh</button>
        </div>
      </div>

      <!-- Add Assignment Form -->
      <div *ngIf="showAddForm" class="add-assignment-form">
        <h3>Create New Assignment</h3>
        <form [formGroup]="assignmentForm" (ngSubmit)="addAssignment()">
          <div class="form-grid">
            <div class="form-group">
              <label for="title">Assignment Title*</label>
              <input id="title" type="text" formControlName="title" placeholder="e.g., Midterm Exam">
              <div class="error" *ngIf="assignmentForm.get('title')?.hasError('required')">
                Title is required
              </div>
            </div>

            <div class="form-group">
              <label for="type">Assignment Type*</label>
              <select id="type" formControlName="type">
                <option value="">Select Type</option>
                <option value="HOMEWORK">Homework</option>
                <option value="QUIZ">Quiz</option>
                <option value="EXAM">Exam</option>
                <option value="PROJECT">Project</option>
                <option value="ESSAY">Essay</option>
                <option value="LAB">Lab Assignment</option>
              </select>
              <div class="error" *ngIf="assignmentForm.get('type')?.hasError('required')">
                Type is required
              </div>
            </div>

            <div class="form-group">
              <label for="totalPoints">Total Points*</label>
              <input id="totalPoints" type="number" formControlName="totalPoints" placeholder="e.g., 100">
              <div class="error" *ngIf="assignmentForm.get('totalPoints')?.hasError('required')">
                Total points is required
              </div>
            </div>

            <div class="form-group">
              <label for="dueDate">Due Date*</label>
              <input id="dueDate" type="datetime-local" formControlName="dueDate">
              <div class="error" *ngIf="assignmentForm.get('dueDate')?.hasError('required')">
                Due date is required
              </div>
            </div>

            <div class="form-group">
              <label for="timeLimit">Time Limit (minutes)</label>
              <input id="timeLimit" type="number" formControlName="timeLimit" placeholder="e.g., 60">
            </div>

            <div class="form-group">
              <label for="latePenalty">Late Penalty (%)</label>
              <input id="latePenalty" type="number" formControlName="latePenalty" placeholder="e.g., 10">
            </div>

            <div class="form-group full-width">
              <label for="description">Description*</label>
              <textarea id="description" formControlName="description" rows="4" placeholder="Assignment description and instructions..."></textarea>
              <div class="error" *ngIf="assignmentForm.get('description')?.hasError('required')">
                Description is required
              </div>
            </div>

            <div class="form-group full-width">
              <label for="instructions">Instructions</label>
              <textarea id="instructions" formControlName="instructions" rows="3" placeholder="Detailed instructions for students..."></textarea>
            </div>

            <div class="form-group checkbox-group">
              <label>
                <input type="checkbox" formControlName="allowsLateSubmission">
                Allow Late Submission
              </label>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" [disabled]="assignmentForm.invalid || isSubmitting" class="submit-btn">
              {{ isSubmitting ? 'Creating...' : 'Create Assignment' }}
            </button>
            <button type="button" (click)="showAddForm = false" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>

      <!-- Assignments List -->
      <div class="assignments-content">
        <div *ngIf="isLoading" class="loading">
          <div class="spinner"></div>
          <p>Loading assignments...</p>
        </div>
        
        <div *ngIf="error" class="error">
          <p>{{ error }}</p>
        </div>
        
        <div *ngIf="!isLoading && !error" class="assignments-grid">
          <div *ngFor="let assignment of assignments" class="assignment-card">
            <div class="assignment-header">
              <h3>{{ assignment.title }}</h3>
              <span class="status-badge" [class]="assignment.status.toLowerCase()">
                {{ assignment.status }}
              </span>
            </div>
            
            <div class="assignment-info">
              <p><strong>Type:</strong> {{ assignment.type }}</p>
              <p><strong>Total Points:</strong> {{ assignment.totalPoints }}</p>
              <p><strong>Due Date:</strong> {{ formatDate(assignment.dueDate) }}</p>
              <p><strong>Created:</strong> {{ formatDate(assignment.createdAt) }}</p>
              
              <div *ngIf="assignment.timeLimit" class="time-info">
                <p><strong>Time Limit:</strong> {{ assignment.timeLimit }} minutes</p>
              </div>
              
              <div *ngIf="assignment.allowLateSubmission" class="late-info">
                <p><strong>Late Submission:</strong> Allowed ({{ assignment.latePenaltyPercentage }}% penalty)</p>
              </div>
              
              <div class="due-status" [class]="getDueStatus(assignment)">
                <p>{{ getDueStatusMessage(assignment) }}</p>
              </div>
              
              <div *ngIf="assignment.description" class="description">
                <p>{{ assignment.description }}</p>
              </div>
            </div>
            
            <div class="assignment-actions">
              <button (click)="viewSubmissions(assignment)" class="view-btn">📋 View Submissions</button>
              <button (click)="editAssignment(assignment)" class="edit-btn">✏️ Edit</button>
              <button (click)="deleteAssignment(assignment.id)" class="delete-btn">🗑️ Delete</button>
            </div>
          </div>
        </div>
        
        <div *ngIf="!isLoading && !error && assignments.length === 0" class="no-assignments">
          <p>No assignments found. Start by creating some assignments!</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .assignments-container {
      padding: 20px;
      max-width: 1400px;
      margin: 0 auto;
      min-height: 100vh;
      background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    }

    .assignments-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      background: white;
      padding: 20px;
      border-radius: 15px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .assignments-header h2 {
      color: #333;
      font-size: 2rem;
      margin: 0;
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }

    .add-btn, .refresh-btn {
      background: #6f42c1;
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

    .add-assignment-form {
      background: white;
      border-radius: 15px;
      padding: 30px;
      margin-bottom: 30px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }

    .add-assignment-form h3 {
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

    .checkbox-group {
      align-items: center;
      flex-direction: row;
    }

    .checkbox-group label {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
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
      border-color: #6f42c1;
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
      background: #6f42c1;
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

    .loading, .error, .no-assignments {
      text-align: center;
      padding: 60px 20px;
      background: white;
      border-radius: 15px;
      margin-bottom: 30px;
    }

    .loading {
      color: #6f42c1;
    }

    .spinner {
      border: 4px solid #f3f3f3;
      border-top: 4px solid #6f42c1;
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

    .no-assignments {
      color: #6c757d;
      background: #f8f9fa;
    }

    .assignments-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 25px;
    }

    .assignment-card {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
      border-left: 5px solid #6f42c1;
      transition: all 0.3s;
    }

    .assignment-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 30px rgba(0,0,0,0.15);
    }

    .assignment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
    }

    .assignment-header h3 {
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

    .status-badge.published {
      background: #d4edda;
      color: #155724;
    }

    .status-badge.draft {
      background: #fff3cd;
      color: #856404;
    }

    .status-badge.closed {
      background: #f8d7da;
      color: #721c24;
    }

    .assignment-info p {
      color: #666;
      margin-bottom: 8px;
      font-size: 0.9rem;
    }

    .assignment-info strong {
      color: #333;
    }

    .time-info, .late-info {
      margin: 10px 0;
      padding: 8px;
      background: #f8f9fa;
      border-radius: 5px;
    }

    .due-status {
      margin: 10px 0;
      padding: 8px;
      border-radius: 5px;
      font-weight: 500;
    }

    .due-status.overdue {
      background: #f8d7da;
      color: #721c24;
    }

    .due-status.due-soon {
      background: #fff3cd;
      color: #856404;
    }

    .due-status.upcoming {
      background: #d1ecf1;
      color: #0c5460;
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

    .assignment-actions {
      display: flex;
      gap: 10px;
      margin-top: 20px;
    }

    .view-btn, .edit-btn, .delete-btn {
      flex: 1;
      padding: 8px 12px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 500;
      font-size: 0.9rem;
      transition: all 0.3s;
    }

    .view-btn {
      background: #17a2b8;
      color: white;
    }

    .edit-btn {
      background: #ffc107;
      color: #212529;
    }

    .delete-btn {
      background: #dc3545;
      color: white;
    }

    .view-btn:hover, .edit-btn:hover, .delete-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 3px 10px rgba(0,0,0,0.2);
    }
  `]
})
export class AssignmentsComponent implements OnInit {
  assignments: Assignment[] = [];
  isLoading = false;
  error: string | null = null;
  showAddForm = false;
  isSubmitting = false;
  assignmentForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.assignmentForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      type: ['', Validators.required],
      totalPoints: [0, Validators.required],
      dueDate: ['', Validators.required],
      assignedDate: [new Date().toISOString()],
      status: ['PUBLISHED'],
      instructions: [''],
      timeLimit: [null],
      allowsLateSubmission: [false],
      latePenalty: [0]
    });
  }

  ngOnInit(): void {
    // Subscribe to cached data for instant loading
    this.apiService.assignments$.subscribe(assignments => {
      this.assignments = assignments;
    });
    this.apiService.assignmentsLoading$.subscribe(loading => {
      this.isLoading = loading;
    });
  }

  loadAssignments(): void {
    this.apiService.refreshAssignments();
  }

  addAssignment(): void {
    if (this.assignmentForm.valid) {
      this.isSubmitting = true;
      
      const assignmentData = {
        ...this.assignmentForm.value,
        course: { id: 1 }, // Default course for now
        attachments: '',
        publishedDate: new Date().toISOString(),
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };

      this.apiService.createAssignment(assignmentData).subscribe({
        next: (newAssignment) => {
          this.assignments.push(newAssignment);
          this.showAddForm = false;
          this.assignmentForm.reset();
          this.isSubmitting = false;
        },
        error: (err) => {
          alert('Failed to create assignment. Please try again.');
          this.isSubmitting = false;
          console.error('Error creating assignment:', err);
        }
      });
    }
  }

  editAssignment(assignment: Assignment): void {
    alert('Edit functionality coming soon! 📝');
  }

  deleteAssignment(id: number): void {
    if (confirm('Are you sure you want to delete this assignment?')) {
      // Optimistic delete - UI updates immediately via service
      this.apiService.deleteAssignment(id).subscribe({
        error: (err) => {
          alert('Failed to delete assignment. Please try again.');
          console.error('Error deleting assignment:', err);
        }
      });
    }
  }

  viewSubmissions(assignment: Assignment): void {
    alert(`Viewing submissions for: ${assignment.title} 📋`);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  }

  getDueStatus(assignment: Assignment): string {
    const dueDate = new Date(assignment.dueDate);
    const now = new Date();
    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    
    if (daysUntilDue < 0) return 'overdue';
    if (daysUntilDue <= 3) return 'due-soon';
    return 'upcoming';
  }

  getDueStatusMessage(assignment: Assignment): string {
    const dueDate = new Date(assignment.dueDate);
    const now = new Date();
    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    
    if (daysUntilDue < 0) return `⚠️ Overdue by ${Math.abs(daysUntilDue)} days`;
    if (daysUntilDue === 0) return '⏰ Due today!';
    if (daysUntilDue === 1) return '⏰ Due tomorrow';
    if (daysUntilDue <= 3) return `⏰ Due in ${daysUntilDue} days`;
    return `📅 Due in ${daysUntilDue} days`;
  }
}
