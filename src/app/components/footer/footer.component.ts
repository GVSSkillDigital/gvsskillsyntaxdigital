import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  template: `
    <footer class="bg-slate-950 text-slate-400 py-16 border-t border-slate-900">
      <div class="max-w-7xl mx-auto px-6 grid grid-cols-1 md:grid-cols-4 gap-12 mb-12">
        <div class="space-y-4">
          <div class="flex items-center space-x-3">
            <div class="logo"><img src="assets/logo.png" class="h-10 w-auto"></div>
          </div>
          <p class="text-xs text-slate-400 leading-relaxed">Empowering software engineers with production-level skills in Java 8, Spring Boot 2.7, MySQL 8, Angular 17, and Cloud Microservices.</p>
        </div>
        <div>
          <h4 class="text-xs font-bold text-slate-200 uppercase tracking-widest mb-4">Quick Navigation</h4>
          <ul class="space-y-2.5 text-xs font-medium">
            <li><a routerLink="/" class="hover:text-amber-400 transition">Home Page</a></li>
            <li><a routerLink="/about" class="hover:text-amber-400 transition">About Our Institute</a></li>
            <li><a routerLink="/courses" class="hover:text-amber-400 transition">Certification Programs</a></li>
            <li><a routerLink="/services" class="hover:text-amber-400 transition">Training Services</a></li>
            <li><a routerLink="/blogs" class="hover:text-amber-400 transition">Tech Blogs</a></li>
          </ul>
        </div>
        <div>
          <h4 class="text-xs font-bold text-slate-200 uppercase tracking-widest mb-4">Technology Stack</h4>
          <p class="text-xs text-slate-400 leading-relaxed">Java 8 Streams & Lambdas, Spring Boot 2.7, Spring Data JPA, MySQL 8, Angular 17, Tailwind CSS.</p>
        </div>
        <div>
          <h4 class="text-xs font-bold text-slate-200 uppercase tracking-widest mb-4">Corporate Office</h4>
          <p class="text-xs text-slate-400 leading-relaxed mb-2">📍 #1024 Digital Training Tower, Tech Corridor</p>
          <p class="text-xs text-slate-400 mb-1">📞 +1 (800) 555-0199</p>
          <p class="text-xs text-slate-400">✉️ admissions&#64;gvdigital.com</p>
        </div>
      </div>
      <div class="max-w-7xl mx-auto px-6 pt-8 border-t border-slate-900 text-center text-xs text-slate-500">
        © 2026 GVDigitalTrainings Institute. All rights reserved.
      </div>
    </footer>
  `
})
export class FooterComponent {}
