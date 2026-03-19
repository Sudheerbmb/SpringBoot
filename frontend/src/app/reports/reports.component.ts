import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="reports-container">
      <div class="reports-header">
        <h2>📊 Academic Reports</h2>
        <div class="header-actions">
          <button (click)="generateReport()" class="generate-btn">📈 Generate Report</button>
          <button (click)="refreshData()" class="refresh-btn">🔄 Refresh</button>
        </div>
      </div>

      <!-- Report Filters -->
      <div class="filters-section">
        <h3>Report Filters</h3>
        <div class="filter-grid">
          <div class="filter-group">
            <label for="reportType">Report Type</label>
            <select id="reportType" [(ngModel)]="selectedReportType">
              <option value="overview">Academic Overview</option>
              <option value="performance">Student Performance</option>
              <option value="attendance">Attendance Report</option>
              <option value="courses">Course Statistics</option>
              <option value="grades">Grade Distribution</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="semester">Semester</label>
            <select id="semester" [(ngModel)]="selectedSemester">
              <option value="all">All Semesters</option>
              <option value="fall2024">Fall 2024</option>
              <option value="spring2024">Spring 2024</option>
              <option value="summer2024">Summer 2024</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="department">Department</label>
            <select id="department" [(ngModel)]="selectedDepartment">
              <option value="all">All Departments</option>
              <option value="cs">Computer Science</option>
              <option value="eng">Engineering</option>
              <option value="business">Business</option>
              <option value="science">Science</option>
            </select>
          </div>

          <div class="filter-group">
            <label for="dateRange">Date Range</label>
            <select id="dateRange" [(ngModel)]="selectedDateRange">
              <option value="30">Last 30 Days</option>
              <option value="90">Last 3 Months</option>
              <option value="180">Last 6 Months</option>
              <option value="365">Last Year</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <h3>{{ totalStudents }}</h3>
            <p>Total Students</p>
          </div>
          <div class="stat-change positive">+12%</div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">📚</div>
          <div class="stat-info">
            <h3>{{ totalCourses }}</h3>
            <p>Active Courses</p>
          </div>
          <div class="stat-change positive">+5%</div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">📝</div>
          <div class="stat-info">
            <h3>{{ totalAssignments }}</h3>
            <p>Pending Assignments</p>
          </div>
          <div class="stat-change negative">-8%</div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <h3>{{ averageGPA }}</h3>
            <p>Average GPA</p>
          </div>
          <div class="stat-change positive">+0.2</div>
        </div>
      </div>

      <!-- Charts Section -->
      <div class="charts-section">
        <div class="chart-container">
          <h3>Grade Distribution</h3>
          <div class="chart-placeholder">
            <div class="grade-bars">
              <div class="grade-bar" *ngFor="let grade of gradeDistribution" [style.width.%]="grade.percentage">
                <span class="grade-label">{{ grade.grade }}: {{ grade.percentage }}%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-container">
          <h3>Course Enrollment Trends</h3>
          <div class="chart-placeholder">
            <div class="trend-chart">
              <div class="trend-bar" *ngFor="let month of enrollmentTrends" [style.height.%]="month.percentage">
                <span class="trend-label">{{ month.month }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Detailed Reports Table -->
      <div class="reports-table-section">
        <h3>Detailed Academic Report</h3>
        <div class="table-container">
          <table class="reports-table">
            <thead>
              <tr>
                <th>Student Name</th>
                <th>Course</th>
                <th>GPA</th>
                <th>Attendance</th>
                <th>Assignments</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let student of studentReports">
                <td>
                  <div class="student-info">
                    <strong>{{ student.name }}</strong>
                    <small>{{ student.email }}</small>
                  </div>
                </td>
                <td>{{ student.course }}</td>
                <td>
                  <span class="gpa-badge" [class]="getGpaClass(student.gpa)">
                    {{ student.gpa }}
                  </span>
                </td>
                <td>
                  <div class="attendance-bar">
                    <div class="attendance-fill" [style.width.%]="student.attendance"></div>
                    <span>{{ student.attendance }}%</span>
                  </div>
                </td>
                <td>{{ student.assignmentsCompleted }}/{{ student.totalAssignments }}</td>
                <td>
                  <span class="status-badge" [class]="student.status.toLowerCase()">
                    {{ student.status }}
                  </span>
                </td>
                <td>
                  <button (click)="viewStudentDetails(student)" class="view-btn">👁️ View</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Export Options -->
      <div class="export-section">
        <h3>Export Reports</h3>
        <div class="export-options">
          <button (click)="exportPDF()" class="export-btn pdf">📄 Export PDF</button>
          <button (click)="exportExcel()" class="export-btn excel">📊 Export Excel</button>
          <button (click)="exportCSV()" class="export-btn csv">📋 Export CSV</button>
          <button (click)="printReport()" class="export-btn print">🖨️ Print</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .reports-container {
      padding: 20px;
      max-width: 1400px;
      margin: 0 auto;
      min-height: 100vh;
      background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
    }

    .reports-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      background: white;
      padding: 20px;
      border-radius: 15px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .reports-header h2 {
      color: #333;
      font-size: 2rem;
      margin: 0;
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }

    .generate-btn, .refresh-btn {
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

    .generate-btn:hover, .refresh-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    }

    .filters-section {
      background: white;
      border-radius: 15px;
      padding: 25px;
      margin-bottom: 30px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .filters-section h3 {
      color: #333;
      margin-bottom: 20px;
    }

    .filter-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
    }

    .filter-group {
      display: flex;
      flex-direction: column;
    }

    .filter-group label {
      color: #333;
      font-weight: 500;
      margin-bottom: 5px;
    }

    .filter-group select {
      padding: 10px;
      border: 2px solid #e9ecef;
      border-radius: 8px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    .filter-group select:focus {
      outline: none;
      border-color: #28a745;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .stat-card {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
      display: flex;
      align-items: center;
      justify-content: space-between;
      transition: transform 0.3s;
    }

    .stat-card:hover {
      transform: translateY(-5px);
    }

    .stat-icon {
      font-size: 2.5rem;
      margin-right: 15px;
    }

    .stat-info h3 {
      color: #333;
      font-size: 2rem;
      margin: 0;
    }

    .stat-info p {
      color: #666;
      margin: 5px 0 0 0;
    }

    .stat-change {
      font-weight: 500;
      font-size: 0.9rem;
    }

    .stat-change.positive {
      color: #28a745;
    }

    .stat-change.negative {
      color: #dc3545;
    }

    .charts-section {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 25px;
      margin-bottom: 30px;
    }

    .chart-container {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .chart-container h3 {
      color: #333;
      margin-bottom: 20px;
    }

    .chart-placeholder {
      height: 200px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .grade-bars {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .grade-bar {
      height: 30px;
      background: linear-gradient(90deg, #28a745, #20c997);
      border-radius: 15px;
      display: flex;
      align-items: center;
      padding: 0 10px;
      color: white;
      font-weight: 500;
      transition: width 0.3s;
    }

    .trend-chart {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: flex-end;
      gap: 10px;
    }

    .trend-bar {
      flex: 1;
      background: linear-gradient(180deg, #17a2b8, #6c757d);
      border-radius: 5px 5px 0 0;
      display: flex;
      align-items: flex-end;
      justify-content: center;
      padding-bottom: 5px;
      color: white;
      font-size: 0.8rem;
    }

    .reports-table-section {
      background: white;
      border-radius: 15px;
      padding: 25px;
      margin-bottom: 30px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .reports-table-section h3 {
      color: #333;
      margin-bottom: 20px;
    }

    .table-container {
      overflow-x: auto;
    }

    .reports-table {
      width: 100%;
      border-collapse: collapse;
    }

    .reports-table th,
    .reports-table td {
      padding: 15px;
      text-align: left;
      border-bottom: 1px solid #e9ecef;
    }

    .reports-table th {
      background: #f8f9fa;
      color: #333;
      font-weight: 600;
    }

    .student-info strong {
      display: block;
      color: #333;
    }

    .student-info small {
      color: #666;
      font-size: 0.8rem;
    }

    .gpa-badge {
      padding: 4px 8px;
      border-radius: 12px;
      font-weight: 500;
      font-size: 0.9rem;
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

    .attendance-bar {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .attendance-fill {
      width: 60px;
      height: 8px;
      background: #e9ecef;
      border-radius: 4px;
      overflow: hidden;
    }

    .attendance-fill div {
      height: 100%;
      background: #28a745;
      transition: width 0.3s;
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

    .view-btn {
      background: #17a2b8;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 0.9rem;
      transition: all 0.3s;
    }

    .view-btn:hover {
      background: #138496;
    }

    .export-section {
      background: white;
      border-radius: 15px;
      padding: 25px;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .export-section h3 {
      color: #333;
      margin-bottom: 20px;
    }

    .export-options {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
    }

    .export-btn {
      padding: 12px 20px;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s;
    }

    .export-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    }

    .export-btn.pdf {
      background: #dc3545;
      color: white;
    }

    .export-btn.excel {
      background: #28a745;
      color: white;
    }

    .export-btn.csv {
      background: #17a2b8;
      color: white;
    }

    .export-btn.print {
      background: #6c757d;
      color: white;
    }
  `]
})
export class ReportsComponent implements OnInit {
  // Statistics
  totalStudents = 156;
  totalCourses = 24;
  totalAssignments = 89;
  averageGPA = 3.6;

  // Filters
  selectedReportType = 'overview';
  selectedSemester = 'all';
  selectedDepartment = 'all';
  selectedDateRange = '90';

  // Chart Data
  gradeDistribution = [
    { grade: 'A', percentage: 35 },
    { grade: 'B', percentage: 40 },
    { grade: 'C', percentage: 20 },
    { grade: 'D', percentage: 4 },
    { grade: 'F', percentage: 1 }
  ];

  enrollmentTrends = [
    { month: 'Jan', percentage: 60 },
    { month: 'Feb', percentage: 75 },
    { month: 'Mar', percentage: 85 },
    { month: 'Apr', percentage: 90 },
    { month: 'May', percentage: 95 },
    { month: 'Jun', percentage: 88 }
  ];

  // Student Reports Data
  studentReports = [
    {
      name: 'John Doe',
      email: 'john.doe@example.com',
      course: 'Computer Science',
      gpa: 3.8,
      attendance: 95,
      assignmentsCompleted: 18,
      totalAssignments: 20,
      status: 'Active'
    },
    {
      name: 'Jane Smith',
      email: 'jane.smith@example.com',
      course: 'Data Science',
      gpa: 3.9,
      attendance: 92,
      assignmentsCompleted: 25,
      totalAssignments: 25,
      status: 'Active'
    },
    {
      name: 'Mike Johnson',
      email: 'mike.johnson@example.com',
      course: 'Business Administration',
      gpa: 3.5,
      attendance: 88,
      assignmentsCompleted: 15,
      totalAssignments: 18,
      status: 'Active'
    },
    {
      name: 'Sarah Williams',
      email: 'sarah.williams@example.com',
      course: 'Engineering',
      gpa: 3.7,
      attendance: 96,
      assignmentsCompleted: 22,
      totalAssignments: 22,
      status: 'Active'
    },
    {
      name: 'David Brown',
      email: 'david.brown@example.com',
      course: 'Cybersecurity',
      gpa: 3.6,
      attendance: 90,
      assignmentsCompleted: 28,
      totalAssignments: 30,
      status: 'Active'
    }
  ];

  ngOnInit(): void {
    // Initialize data
  }

  generateReport(): void {
    alert('Generating comprehensive report... 📊\n\nReport will include:\n• Academic performance metrics\n• Attendance statistics\n• Grade distribution analysis\n• Course enrollment trends');
  }

  refreshData(): void {
    alert('Refreshing report data... 🔄\n\nLatest data from database will be loaded.');
  }

  viewStudentDetails(student: any): void {
    alert(`Student Details:\n\n📋 Name: ${student.name}\n📧 Email: ${student.email}\n📚 Course: ${student.course}\n📊 GPA: ${student.gpa}\n📈 Attendance: ${student.attendance}%\n📝 Assignments: ${student.assignmentsCompleted}/${student.totalAssignments}\n🏷️ Status: ${student.status}`);
  }

  getGpaClass(gpa: number): string {
    if (gpa >= 3.7) return 'excellent';
    if (gpa >= 3.3) return 'good';
    if (gpa >= 2.7) return 'average';
    return 'poor';
  }

  exportPDF(): void {
    alert('📄 Exporting report as PDF...\n\nThe report will be downloaded as a formatted PDF file.');
  }

  exportExcel(): void {
    alert('📊 Exporting report as Excel...\n\nThe report will be downloaded as an Excel spreadsheet with all data.');
  }

  exportCSV(): void {
    alert('📋 Exporting report as CSV...\n\nThe report will be downloaded as a CSV file for data analysis.');
  }

  printReport(): void {
    alert('🖨️ Preparing report for printing...\n\nThe report will be formatted for printing with proper layout and styling.');
    window.print();
  }
}
