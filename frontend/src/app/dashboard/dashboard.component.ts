import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Learning Management System</h1>
        <p>Welcome to your dashboard!</p>
      </div>
      
      <div class="dashboard-content">
        <div class="welcome-card">
          <h2>🎓 Welcome to LMS</h2>
          <p>Your Learning Management System is ready to use!</p>
          
          <div class="features-grid">
            <div class="feature-card">
              <h3>📚 Courses</h3>
              <p>Manage and view courses</p>
              <button (click)="navigateToCourses()" class="feature-btn">View Courses</button>
            </div>
            
            <div class="feature-card">
              <h3>👥 Students</h3>
              <p>Manage student records</p>
              <button (click)="navigateToStudents()" class="feature-btn">View Students</button>
            </div>
            
            <div class="feature-card">
              <h3>📝 Assignments</h3>
              <p>Create and manage assignments</p>
              <button (click)="navigateToAssignments()" class="feature-btn">View Assignments</button>
            </div>
            
            <div class="feature-card">
              <h3>📊 Reports</h3>
              <p>View academic reports</p>
              <button (click)="navigateToReports()" class="feature-btn">View Reports</button>
            </div>
          </div>
        </div>
        
        <div class="logout-section">
          <button (click)="logout()" class="logout-btn">Logout</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .dashboard-header {
      text-align: center;
      color: white;
      margin-bottom: 40px;
    }

    .dashboard-header h1 {
      font-size: 2.5rem;
      margin-bottom: 10px;
    }

    .dashboard-header p {
      font-size: 1.2rem;
      opacity: 0.9;
    }

    .dashboard-content {
      max-width: 1200px;
      margin: 0 auto;
    }

    .welcome-card {
      background: white;
      border-radius: 15px;
      padding: 40px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.2);
      margin-bottom: 30px;
    }

    .welcome-card h2 {
      text-align: center;
      color: #333;
      margin-bottom: 20px;
      font-size: 2rem;
    }

    .welcome-card p {
      text-align: center;
      color: #666;
      margin-bottom: 40px;
      font-size: 1.1rem;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .feature-card {
      background: #f8f9fa;
      border-radius: 10px;
      padding: 25px;
      text-align: center;
      border: 2px solid #e9ecef;
      transition: transform 0.3s, box-shadow 0.3s;
    }

    .feature-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .feature-card h3 {
      color: #333;
      margin-bottom: 10px;
      font-size: 1.3rem;
    }

    .feature-card p {
      color: #666;
      margin-bottom: 20px;
    }

    .feature-btn {
      background: #667eea;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 5px;
      cursor: pointer;
      font-weight: 500;
      transition: background 0.3s;
    }

    .feature-btn:hover {
      background: #5a6fd8;
    }

    .logout-section {
      text-align: center;
    }

    .logout-btn {
      background: #e74c3c;
      color: white;
      border: none;
      padding: 12px 30px;
      border-radius: 5px;
      cursor: pointer;
      font-weight: 500;
      transition: background 0.3s;
    }

    .logout-btn:hover {
      background: #c0392b;
    }
  `]
})
export class DashboardComponent implements OnInit {

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Check if user is authenticated
    const token = localStorage.getItem('token');
    if (!token) {
      this.router.navigate(['/login']);
    }
  }

  navigateToCourses(): void {
    this.router.navigate(['/courses']);
  }

  navigateToStudents(): void {
    this.router.navigate(['/students']);
  }

  navigateToAssignments(): void {
    this.router.navigate(['/assignments']);
  }

  navigateToReports(): void {
    this.router.navigate(['/reports']);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
