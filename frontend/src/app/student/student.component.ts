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

  newStudent: Student = {
    id: undefined,
    name: '',
    email: ''
  };

  isEditMode = false;

  constructor(private service: StudentService) {
    this.loadStudents();
  }

  // ✅ LOAD ALL
  loadStudents() {
    this.service.getStudents().subscribe(data => {
      this.students = data;
    });
  }

  // ✅ ADD
  add() {
    this.service.addStudent(this.newStudent).subscribe(() => {
      this.resetForm();
      this.loadStudents();
    });
  }

  // ✅ DELETE
  delete(id: number) {
    this.service.deleteStudent(id).subscribe(() => {
      this.loadStudents();
    });
  }

  // ✅ EDIT (fill form)
  edit(student: Student) {
    this.newStudent = { ...student };
    this.isEditMode = true;
  }

  // ✅ UPDATE
  update() {
    if (!this.newStudent.id) return;

    this.service.updateStudent(this.newStudent.id, this.newStudent)
      .subscribe(() => {
        this.resetForm();
        this.loadStudents();
      });
  }

  // ✅ RESET FORM
  resetForm() {
    this.newStudent = { id: undefined, name: '', email: '' };
    this.isEditMode = false;
  }
}