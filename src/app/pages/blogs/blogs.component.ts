import { Component } from '@angular/core';

@Component({
  selector: 'app-blogs',
  standalone: true,
  template: `
    <div class="py-20 max-w-7xl mx-auto px-6">
      <div class="text-center max-w-3xl mx-auto mb-16 space-y-4">
        <h1 class="text-4xl font-black text-slate-900 tracking-tight">Tech Blogs & Industry Insights</h1>
        <p class="text-slate-600 text-base">Expert architectural deep dives written by our senior engineering instructors.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="bg-white border border-slate-200 rounded-3xl p-8 shadow-sm flex flex-col justify-between">
          <div class="space-y-3">
            <span class="text-xs font-bold px-3 py-1 bg-amber-50 text-amber-700 rounded-full">Java 8 & Spring Boot</span>
            <h3 class="text-lg font-bold text-slate-900">Mastering Java 8 Streams & Collector Customization in Spring Boot 2.7</h3>
            <p class="text-xs text-slate-600 leading-relaxed">Optimize database payloads and high-throughput transformations using advanced functional pipelines.</p>
          </div>
          <div class="pt-6 border-t border-slate-100 flex justify-between items-center text-xs text-slate-500 font-medium">
            <span>Oct 28, 2026</span>
            <span class="text-indigo-600 font-bold">7 min read</span>
          </div>
        </div>

        <div class="bg-white border border-slate-200 rounded-3xl p-8 shadow-sm flex flex-col justify-between">
          <div class="space-y-3">
            <span class="text-xs font-bold px-3 py-1 bg-indigo-50 text-indigo-700 rounded-full">MySQL JPA</span>
            <h3 class="text-lg font-bold text-slate-900">Optimizing Hibernate JPA Lazy Loading and N+1 Queries</h3>
            <p class="text-xs text-slate-600 leading-relaxed">Techniques to inspect SQL execution plans and leverage Entity Graphs for ultra-fast database retrieval.</p>
          </div>
          <div class="pt-6 border-t border-slate-100 flex justify-between items-center text-xs text-slate-500 font-medium">
            <span>Oct 15, 2026</span>
            <span class="text-indigo-600 font-bold">6 min read</span>
          </div>
        </div>

        <div class="bg-white border border-slate-200 rounded-3xl p-8 shadow-sm flex flex-col justify-between">
          <div class="space-y-3">
            <span class="text-xs font-bold px-3 py-1 bg-green-50 text-green-700 rounded-full">Angular Architecture</span>
            <h3 class="text-lg font-bold text-slate-900">Modern Angular State Management with Reactive Services</h3>
            <p class="text-xs text-slate-600 leading-relaxed">Clean client-side architecture patterns connecting seamlessly with Spring Boot REST backends.</p>
          </div>
          <div class="pt-6 border-t border-slate-100 flex justify-between items-center text-xs text-slate-500 font-medium">
            <span>Sep 22, 2026</span>
            <span class="text-indigo-600 font-bold">5 min read</span>
          </div>
        </div>
      </div>
    </div>
  `
})
export class BlogsComponent {}
