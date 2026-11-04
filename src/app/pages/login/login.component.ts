import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="py-24 flex justify-center px-6">
      <div class="bg-white p-10 rounded-3xl border border-slate-200 shadow-xl max-w-md w-full space-y-6">
        <div class="text-center space-y-2">
          <div class="w-12 h-12 bg-indigo-600 text-white rounded-2xl flex items-center justify-center font-bold text-xl mx-auto shadow-md">GV</div>
          <h1 class="text-2xl font-black text-slate-900 tracking-tight">Portal Login</h1>
          <p class="text-xs text-slate-500">Access your student portal & cloud lab workspaces.</p>
        </div>
        <form (ngSubmit)="onLogin()" class="space-y-4">
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Username / Email</label>
            <input [(ngModel)]="username" name="username" class="w-full px-4 py-3 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50" required placeholder="student&#64;gvdigital.com" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Password</label>
            <input [(ngModel)]="password" name="password" type="password" class="w-full px-4 py-2.5 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:border-indigo-600 bg-slate-50/50" required placeholder="••••••••" />
          </div>
          <button type="submit" class="w-full py-4 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold rounded-2xl text-sm shadow-lg transition transform hover:-translate-y-0.5">Sign In</button>
        </form>
      </div>
    </div>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  onLogin() {
    alert(`Successfully signed in as: ${this.username}`);
  }
}
