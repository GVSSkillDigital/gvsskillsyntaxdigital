import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TrainingService, Course } from '../../services/training.service';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="py-20 max-w-7xl mx-auto px-6">
      <div class="text-center max-w-3xl mx-auto mb-16 space-y-4">
        <h1 class="text-4xl font-black text-slate-900 tracking-tight">Courses & MySQL Database Catalog</h1>
        <p class="text-slate-600 text-base">Select your certification track powered live by our Spring Boot backend and MySQL database.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        @for (c of courses(); track c.id) {
          <div class="bg-white border border-slate-200 rounded-3xl p-8 flex flex-col justify-between shadow-sm hover:shadow-xl transition duration-300">
            <div>
              <span class="text-xs font-bold px-3 py-1 bg-indigo-50 text-indigo-700 rounded-full">{{ c.category }}</span>
              <h3 class="text-xl font-bold text-slate-900 mt-4 mb-2">{{ c.title }}</h3>
              <p class="text-xs font-mono text-indigo-600 font-semibold mb-1">Course Code: {{ c.code }}</p>
              <p class="text-xs text-slate-500 font-medium mb-3">Duration: {{ c.duration }} | Mode: {{ c.mode }}</p>
              <p class="text-xs text-slate-600 leading-relaxed mb-6">{{ c.description }}</p>
            </div>
            <div class="pt-6 border-t border-slate-100 flex justify-between items-center">
              <span class="text-xl font-black text-slate-900">{{ c.fee }}</span>
              <a [routerLink]="['/register']" [queryParams]="{code: c.code}" class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl shadow-md transition">Enroll Now</a>
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class CoursesComponent implements OnInit {
  trainingService = inject(TrainingService);
  courses = signal<Course[]>([]);

  ngOnInit() {
    this.trainingService.getCourses().subscribe(data => this.courses.set(data));
  }
}
