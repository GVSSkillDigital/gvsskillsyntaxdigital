import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="py-20 max-w-7xl mx-auto px-6">
      <div class="text-center max-w-3xl mx-auto mb-16 space-y-4">
        <h1 class="text-4xl font-black text-slate-900 tracking-tight">Our Training Services</h1>
        <p class="text-slate-600 text-base">Comprehensive technical growth solutions for individual software engineers and enterprise engineering divisions.</p>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between hover:shadow-lg transition">
          <div class="space-y-4">
            <div class="w-14 h-14 bg-indigo-50 text-indigo-600 rounded-2xl flex items-center justify-center text-2xl font-bold">🎓</div>
            <h3 class="text-xl font-bold text-slate-900">Certification Bootcamps</h3>
            <p class="text-slate-600 text-sm leading-relaxed">Live instructor-led training in Java 8, Spring Boot 2.7, MySQL 8, and Angular with real enterprise projects.</p>
          </div>
          <div class="pt-8">
            <a routerLink="/courses" class="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs block text-center shadow-md transition">Browse Catalog</a>
          </div>
        </div>

        <div class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between hover:shadow-lg transition">
          <div class="space-y-4">
            <div class="w-14 h-14 bg-amber-50 text-amber-600 rounded-2xl flex items-center justify-center text-2xl font-bold">🏢</div>
            <h3 class="text-xl font-bold text-slate-900">Corporate Upskilling</h3>
            <p class="text-slate-600 text-sm leading-relaxed">Customized enterprise curriculum for software development teams migrating legacy monoliths to Spring Boot microservices.</p>
          </div>
          <div class="pt-8">
            <button (click)="alertCorp()" class="w-full py-3 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl text-xs shadow-md transition">Inquire for Teams</button>
          </div>
        </div>

        <div class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between hover:shadow-lg transition">
          <div class="space-y-4">
            <div class="w-14 h-14 bg-emerald-50 text-emerald-600 rounded-2xl flex items-center justify-center text-2xl font-bold">💼</div>
            <h3 class="text-xl font-bold text-slate-900">Placement Assistance</h3>
            <p class="text-slate-600 text-sm leading-relaxed">Resume restructuring, mock technical interviews, and direct referrals to top tech hiring partners.</p>
          </div>
          <div class="pt-8">
            <a routerLink="/register" class="w-full py-3 bg-amber-500 hover:bg-amber-600 text-slate-950 font-extrabold rounded-xl text-xs block text-center shadow-md transition">Get Career Support</a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ServicesComponent {
  alertCorp() {
    alert("Corporate training inquiry submitted. Our team will contact your engineering division shortly.");
  }
}
