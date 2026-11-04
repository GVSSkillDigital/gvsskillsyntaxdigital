import { Component, inject, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TrainingService, Course } from '../../services/training.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="py-20 max-w-xl mx-auto px-6">
      <div class="text-center mb-10 space-y-2">
        <h1 class="text-3xl font-black text-slate-900 tracking-tight">Student Registration Form</h1>
        <p class="text-slate-600 text-sm">Secure your seat in our upcoming batch. Saved directly to MySQL via Spring Boot.</p>
      </div>

      @if (submitted()) {
        <div class="bg-green-50 border border-green-200 p-8 rounded-3xl text-center space-y-4 shadow-sm">
          <div class="w-14 h-14 bg-green-500 text-white rounded-full flex items-center justify-center text-2xl mx-auto shadow-md">✓</div>
          <h2 class="text-2xl font-bold text-slate-900">Registration Saved in MySQL!</h2>
          <p class="text-xs text-slate-600">Your enrollment record has been successfully committed to the database.</p>
          <button (click)="submitted.set(false)" class="px-6 py-3 bg-indigo-600 text-white rounded-xl text-xs font-bold shadow-md">Register Another Student</button>
        </div>
      } @else {
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-5">
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Full Name *</label>
            <input formControlName="fullName" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50" placeholder="John Doe" />
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Email *</label>
              <input formControlName="email" type="email" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50" placeholder="john&#64;example.com" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Phone *</label>
              <input formControlName="phone" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50" placeholder="+1 555-0199" />
            </div>
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Select Course *</label>
            <select formControlName="courseCode" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50">
              @for (c of courses(); track c.id) {
                <option [value]="c.code">{{ c.title }} ({{ c.fee }})</option>
              }
            </select>
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Payment Method</label>
            <select formControlName="paymentMethod" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50">
              <option value="Online Credit Card">Online Credit Card</option>
              <option value="Installment EMI">Installment EMI</option>
            </select>
          </div>
          <button type="submit" [disabled]="form.invalid" class="w-full py-4 bg-amber-500 hover:bg-amber-600 disabled:opacity-50 text-slate-950 font-extrabold rounded-2xl text-sm shadow-lg transition transform hover:-translate-y-0.5">
            Submit Registration
          </button>
        </form>
      }
    </div>
  `
})
export class RegisterComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  trainingService = inject(TrainingService);

  courses = signal<Course[]>([]);
  submitted = signal(false);
  form!: FormGroup;

  ngOnInit() {
    const code = this.route.snapshot.queryParamMap.get('code') || 'JAVA-01';
    this.trainingService.getCourses().subscribe(data => this.courses.set(data));

    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      courseCode: [code, Validators.required],
      paymentMethod: ['Online Credit Card', Validators.required]
    });
  }

  onSubmit() {
    if (this.form.valid) {
      this.trainingService.registerStudent(this.form.value).subscribe(() => {
        this.submitted.set(true);
      });
    }
  }
}
