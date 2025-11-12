import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { ApiarioService, Apiario } from '../../../services/apiario.service';
import { FuncionarioService, FuncionarioCreateDTO } from '../../../services/funcionario.service';

@Component({
  selector: 'app-funcionario-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule],
  template: `
  <div class="container py-4">
    <h2 class="mb-3">Cadastro de Funcionário</h2>

    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="row g-3">
      <div class="col-md-6">
        <label class="form-label">Nome</label>
        <input class="form-control" formControlName="nome" placeholder="Nome completo" />
      </div>
      <div class="col-md-6">
        <label class="form-label">Usuário</label>
        <input class="form-control" formControlName="username" placeholder="Login do funcionário" />
      </div>
      <div class="col-md-6">
        <label class="form-label">Email</label>
        <input type="email" class="form-control" formControlName="email" placeholder="email@exemplo.com" />
      </div>
      <div class="col-md-6">
        <label class="form-label">Senha</label>
        <input type="password" class="form-control" formControlName="password" placeholder="Senha inicial" />
      </div>
      <div class="col-md-6">
        <label class="form-label">Apiário</label>
        <select class="form-select" formControlName="apiarioId" (change)="onApiarioChange()">
          <option [ngValue]="null">Selecione um apiário</option>
          <option *ngFor="let a of apiarios" [ngValue]="a.id">{{ a.nome }}</option>
        </select>
      </div>
      <div class="col-12">
        <button class="btn btn-primary" type="submit" [disabled]="form.invalid || submitting">Cadastrar Funcionário</button>
        <span class="ms-3" *ngIf="message" [class.text-success]="success" [class.text-danger]="!success">{{ message }}</span>
      </div>
    </form>

    <div class="mt-4" *ngIf="funcionarios.length">
      <h5>Funcionários do apiário selecionado</h5>
      <div class="table-responsive">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Usuário</th>
              <th>Email</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let f of funcionarios">
              <td>{{ f.nome }}</td>
              <td>{{ f.username }}</td>
              <td>{{ f.email }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="mt-3">
      <a routerLink="/dashboard" class="btn btn-outline-secondary">Voltar ao Dashboard</a>
    </div>
  </div>
  `
})
export class FuncionarioCadastroComponent implements OnInit {
  form!: FormGroup;

  apiarios: Apiario[] = [];
  funcionarios: any[] = [];
  submitting = false;
  message = '';
  success = true;

  constructor(
    private fb: FormBuilder,
    private apiarioService: ApiarioService,
    private funcionarioService: FuncionarioService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2)]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4)]],
      apiarioId: [null as number | null, [Validators.required]]
    });
    this.apiarioService.getAll().subscribe({
      next: (list) => (this.apiarios = list ?? []),
      error: () => (this.apiarios = [])
    });
  }

  onApiarioChange(): void {
    const apiarioId = this.form.value.apiarioId;
    if (apiarioId) {
      this.funcionarioService.listByApiario(apiarioId).subscribe({
        next: (res) => (this.funcionarios = res ?? []),
        error: () => (this.funcionarios = [])
      });
    } else {
      this.funcionarios = [];
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const dto: FuncionarioCreateDTO = this.form.value as any;
    this.submitting = true;
    this.message = '';
    this.funcionarioService.create(dto).subscribe({
      next: () => {
        this.success = true;
        this.message = 'Funcionário cadastrado com sucesso';
        this.submitting = false;
        this.onApiarioChange();
        this.form.patchValue({ nome: '', username: '', email: '', password: '' });
      },
      error: (err) => {
        this.success = false;
        if (err?.status === 401) {
          this.message = 'Acesso não autorizado';
        } else if (err?.status === 403) {
          this.message = 'Permissão insuficiente';
        } else {
          this.message = err?.error?.error || 'Falha ao cadastrar funcionário';
        }
        this.submitting = false;
      }
    });
  }
}