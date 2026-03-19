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

  loadStudents() {
    this.service.getStudents().subscribe(data => {
      this.students = data;
    });
  }

  // ⚡ INSTANT ADD
  add() {
    const temp = { ...this.newStudent };

    this.students.push(temp);

    this.service.addStudent(this.newStudent).subscribe(res => {
      Object.assign(temp, res);
    });

    this.resetForm();
  }

  // ⚡ INSTANT DELETE
  delete(id: number) {
    this.students = this.students.filter(s => s.id !== id);
    this.service.deleteStudent(id).subscribe();
  }

  // EDIT
  edit(student: Student) {
    this.newStudent = { ...student };
    this.isEditMode = true;
  }

  // ⚡ INSTANT UPDATE
  update() {
    if (!this.newStudent.id) return;

    const index = this.students.findIndex(s => s.id === this.newStudent.id);

    if (index !== -1) {
      this.students[index] = { ...this.newStudent };
    }

    this.service.updateStudent(this.newStudent.id, this.newStudent).subscribe();

    this.resetForm();
  }

  resetForm() {
    this.newStudent = { id: undefined, name: '', email: '' };
    this.isEditMode = false;
  }

  trackById(index: number, item: Student) {
    return item.id;
  }
}