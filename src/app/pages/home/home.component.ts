import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TrainingService, Course } from '../../services/training.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="bg-gradient-to-br from-slate-950 via-indigo-950 to-slate-900 text-white py-24 lg:py-32 relative overflow-hidden">
      <div class="max-w-7xl mx-auto px-6 relative z-10 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
        <div class="space-y-6">
          <span class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-bold tracking-wide uppercase">
            ✨ Java 8 & Spring Boot 2.7 Certified Curriculum
          </span>
          <h1 class="text-4xl sm:text-6xl font-black tracking-tight leading-tight">
            Master Enterprise Microservices & <span class="text-amber-400">Full-Stack Engineering</span>
          </h1>
          <p class="text-slate-300 text-base sm:text-lg leading-relaxed font-normal">
            Accelerate your career with rigorous hands-on training in Java 8 Streams & Lambdas, Spring Boot, MySQL JPA, Angular 17, and Cloud DevOps.
          </p>
          <div class="flex flex-col sm:flex-row gap-4 pt-4">
            <a routerLink="/register" class="px-8 py-4 bg-amber-500 hover:bg-amber-600 text-slate-950 font-extrabold rounded-2xl text-center shadow-xl transition transform hover:-translate-y-0.5">Register for Batch &rarr;</a>
            <a routerLink="/courses" class="px-8 py-4 bg-slate-900/80 hover:bg-slate-800 text-white font-bold rounded-2xl text-center border border-slate-700 backdrop-blur-md transition">Explore Course Catalog</a>
          </div>
        </div>

        <div class="bg-slate-900/60 border border-slate-800 p-8 rounded-3xl backdrop-blur-xl shadow-2xl relative space-y-6">
          <div class="flex justify-between items-center border-b border-slate-800 pb-4">
            <div>
              <h3 class="text-xl font-bold text-white">Institute Placement Metrics</h3>
              <p class="text-xs text-slate-400">Real-time MySQL records</p>
            </div>
            <span class="bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold px-3 py-1 rounded-full">Active Sync</span>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-slate-950 p-5 rounded-2xl border border-slate-800/80">
              <div class="text-3xl font-black text-amber-400">6,500+</div>
              <div class="text-xs text-slate-400 mt-1 font-medium">Engineers Graduated</div>
            </div>
            <div class="bg-slate-950 p-5 rounded-2xl border border-slate-800/80">
              <div class="text-3xl font-black text-indigo-400">96%</div>
              <div class="text-xs text-slate-400 mt-1 font-medium">Placement Rate</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-20 max-w-7xl mx-auto px-6">
      <div class="text-center max-w-2xl mx-auto mb-16">
        <h2 class="text-3xl font-black text-slate-900 tracking-tight">Featured MySQL Database Programs</h2>
        <p class="text-slate-600 text-sm mt-3">Hand-crafted enterprise microservice bootcamps loaded live from MySQL.</p>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        @for (c of courses().slice(0, 3); track c.id) {
          <div class="bg-white border border-slate-200/80 rounded-3xl p-8 flex flex-col justify-between shadow-sm hover:shadow-xl transition duration-300">
            <div>
              <span class="text-xs font-bold px-3 py-1 bg-indigo-50 text-indigo-700 rounded-full">{{ c.category }}</span>
              <h3 class="text-xl font-bold text-slate-900 mt-4 mb-2">{{ c.title }}</h3>
              <p class="text-xs font-mono text-indigo-600 font-semibold mb-3">Code: {{ c.code }} | {{ c.duration }}</p>
              <p class="text-xs text-slate-600 leading-relaxed mb-6">{{ c.description }}</p>
            </div>
            <div class="pt-6 border-t border-slate-100 flex justify-between items-center">
              <span class="text-xl font-black text-slate-900">{{ c.fee }}</span>
              <a [routerLink]="['/register']" [queryParams]="{code: c.code}" class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl shadow-md transition">Enroll Now</a>
            </div>
          </div>
        }
      </div>
    </section>
  `
})
export class HomeComponent implements OnInit {
  trainingService = inject(TrainingService);
  courses = signal<Course[]>([]);

  ngOnInit() {
    this.trainingService.getCourses().subscribe(data => this.courses.set(data));
  }
}
