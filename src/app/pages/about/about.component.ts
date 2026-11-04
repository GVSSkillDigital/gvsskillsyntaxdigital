import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  standalone: true,
  template: `
    <div class="py-20 max-w-5xl mx-auto px-6">
      <div class="text-center max-w-3xl mx-auto mb-16 space-y-4">
        <span class="text-xs font-bold uppercase tracking-widest text-indigo-600 bg-indigo-50 px-4 py-1.5 rounded-full">About GVDigitalTrainings</span>
        <h1 class="text-4xl font-black text-slate-900 tracking-tight">Engineering Excellence Through Practical Microservices</h1>
        <p class="text-slate-600 text-base leading-relaxed">
          Founded by enterprise architects, GVDigitalTrainings provides rigorous, production-grade learning experiences bridging academia and corporate software development.
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-16">
        <div class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-3">
          <div class="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-2xl flex items-center justify-center text-xl font-bold mb-4">🚀</div>
          <h3 class="text-xl font-bold text-slate-900">Our Core Mission</h3>
          <p class="text-slate-600 text-sm leading-relaxed">To cultivate top-tier engineering talent proficient in Java 8 functional programming, Spring Boot frameworks, MySQL persistence, and Angular 17 client architectures.</p>
        </div>
        <div class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-3">
          <div class="w-12 h-12 bg-amber-50 text-amber-600 rounded-2xl flex items-center justify-center text-xl font-bold mb-4">💻</div>
          <h3 class="text-xl font-bold text-slate-900">Cloud Labs & MySQL Integration</h3>
          <p class="text-slate-600 text-sm leading-relaxed">Every student receives isolated cloud workspaces connected to live MySQL instances to practice database optimization, transaction management, and REST APIs.</p>
        </div>
      </div>
    </div>
  `
})
export class AboutComponent {}
