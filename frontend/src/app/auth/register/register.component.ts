import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  error = '';
  loading = false;
  success = false;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      nome: ['', Validators.required],
      sobrenome: ['', Validators.required],
      username: ['', [Validators.required, Validators.minLength(4)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      role: ['ROLE_APICULTOR', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  // Getter para acessar os controles do formulário facilmente
  get f() {
    return this.registerForm.controls;
  }

  // Validador customizado para verificar se as senhas coincidem
  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password');
    const confirmPassword = formGroup.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mustMatch: true });
    } else {
      if (confirmPassword?.errors?.['mustMatch']) {
        const errors = { ...confirmPassword.errors };
        delete errors['mustMatch'];
        confirmPassword.setErrors(Object.keys(errors).length ? errors : null);
      }
    }
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';

    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;

    const { nome, sobrenome, username, email, password, role } = this.registerForm.value;
    const fullName = `${nome} ${sobrenome}`.trim();
    const backendRole = role;

    this.authService.register({ nome: fullName, username, email, password, role: backendRole }).subscribe({
      next: () => {
        this.success = true;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err: any) => {
        this.error = (err?.error && typeof err.error === 'string')
          ? err.error
          : 'Falha no registro. Tente novamente.';
        this.loading = false;
        this.submitted = false;
      }
    });
  }
}