import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService, Student } from './student.service';

@Component({
  selector: 'app-student',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student.component.html'
})
export class StudentComponent {

  students: Student[] = [];
  newStudent: Student = { name: '', email: '' };

  constructor(private service: StudentService) {
    this.loadStudents();
  }

  loadStudents() {
    this.service.getStudents().subscribe(data => {
      this.students = data;
    });
  }

  add() {
    this.service.addStudent(this.newStudent).subscribe(() => {
      this.newStudent = { name: '', email: '' };
      this.loadStudents();
    });
  }

  delete(id: number) {
    this.service.deleteStudent(id).subscribe(() => {
      this.loadStudents();
    });
  }
}