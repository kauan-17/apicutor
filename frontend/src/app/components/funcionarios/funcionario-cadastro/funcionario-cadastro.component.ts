import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { ApiarioService, Apiario } from '../../../services/apiario.service';
import { FuncionarioService, FuncionarioCreateDTO } from '../../../services/funcionario.service';
import { forkJoin } from 'rxjs';

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
      <div class="col-md-12">
        <label class="form-label">Vincular a apiários</label>
        <div class="row g-2 align-items-start">
          <div class="col-md-5">
            <div class="form-text mb-1">Disponíveis</div>
            <select #availSel class="form-select" multiple [size]="extraSelectSize">
              <option *ngFor="let a of availableExtras" [value]="a.id" (dblclick)="addExtra(a.id)">{{ a.nome }}</option>
            </select>
          </div>
          <div class="col-md-2 d-grid gap-2">
            <button type="button" class="btn btn-primary" (click)="addSelected(availSel)">Adicionar →</button>
            <button type="button" class="btn btn-secondary" (click)="removeSelected(selSel)">← Remover</button>
          </div>
          <div class="col-md-5">
            <div class="form-text mb-1">Selecionados</div>
            <select #selSel class="form-select" multiple [size]="extraSelectSize">
              <option *ngFor="let a of selectedExtras" [value]="a.id" (dblclick)="removeExtra(a.id)">{{ a.nome }}</option>
            </select>
          </div>
        </div>
      </div>
      <div class="col-12">
        <button class="btn btn-primary me-2" type="submit" [disabled]="form.invalid || submitting">Cadastrar Funcionário</button>
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
        <a routerLink="/dashboard" class="btn btn-secondary">Voltar ao Dashboard</a>
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
      apiarioId: [null as number | null],
      apiarioIdsExtras: [[] as number[]]
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
      // Remover o selecionado principal dos extras, se presente
      const extras: number[] = (this.form.value.apiarioIdsExtras ?? []).filter((id: number) => id !== apiarioId);
      this.form.patchValue({ apiarioIdsExtras: extras });
    } else {
      this.funcionarios = [];
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { apiarioIdsExtras, ...rest } = this.form.value as any;
    const extras: number[] = (apiarioIdsExtras ?? []) as number[];
    if (!extras.length) {
      this.success = false;
      this.message = 'Selecione pelo menos um apiário';
      return;
    }
    // O primeiro extra será o principal para criação
    const principalId = extras[0];
    const extrasToAssign = extras.slice(1);
    const dto: FuncionarioCreateDTO = { ...rest, apiarioId: principalId };
    this.submitting = true;
    this.message = '';
    this.funcionarioService.create(dto).subscribe({
      next: (res) => {
        const funcionarioId = res?.id;
        if (funcionarioId && extrasToAssign.length) {
          forkJoin(extrasToAssign.map((apiarioId: number) => this.funcionarioService.atribuir(funcionarioId, apiarioId))).subscribe({
            next: () => {
              this.success = true;
              this.message = 'Funcionário cadastrado e vinculado aos apiários selecionados';
              this.submitting = false;
              this.form.patchValue({ nome: '', username: '', email: '', password: '', apiarioIdsExtras: [], apiarioId: null });
            },
            error: () => {
              this.success = true; // cadastro foi criado; apenas vinculação extra falhou
              this.message = 'Funcionário criado, mas houve falha ao vincular alguns apiários';
              this.submitting = false;
            }
          });
        } else {
          this.success = true;
          this.message = 'Funcionário cadastrado com sucesso';
          this.submitting = false;
          this.form.patchValue({ nome: '', username: '', email: '', password: '', apiarioIdsExtras: [], apiarioId: null });
        }
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

  get extraSelectSize(): number {
    return Math.min(this.apiarios.length, 6);
  }

  get availableExtras(): Apiario[] {
    const extras: number[] = this.form.value.apiarioIdsExtras ?? [];
    return (this.apiarios ?? []).filter(a => !extras.includes(a.id));
  }

  get selectedExtras(): Apiario[] {
    const extras: number[] = this.form.value.apiarioIdsExtras ?? [];
    const byId = new Map<number, Apiario>((this.apiarios ?? []).map(a => [a.id, a] as [number, Apiario]));
    return extras.map(id => byId.get(id)).filter((a): a is Apiario => !!a);
  }

  addExtra(id: number): void {
    let extras: number[] = (this.form.value.apiarioIdsExtras ?? []).slice();
    if (!extras.includes(id)) {
      extras.push(id);
      this.form.patchValue({ apiarioIdsExtras: extras });
    }
  }

  removeExtra(id: number): void {
    let extras: number[] = (this.form.value.apiarioIdsExtras ?? []).slice();
    const idx = extras.indexOf(id);
    if (idx >= 0) {
      extras.splice(idx, 1);
      this.form.patchValue({ apiarioIdsExtras: extras });
    }
  }

  addSelected(selectEl: HTMLSelectElement): void {
    const ids = Array.from(selectEl.selectedOptions).map(opt => Number(opt.value));
    let extras: number[] = (this.form.value.apiarioIdsExtras ?? []).slice();
    ids.forEach(id => {
      if (!extras.includes(id)) extras.push(id);
    });
    this.form.patchValue({ apiarioIdsExtras: extras });
  }

  removeSelected(selectEl: HTMLSelectElement): void {
    const ids = Array.from(selectEl.selectedOptions).map(opt => Number(opt.value));
    let extras: number[] = (this.form.value.apiarioIdsExtras ?? []).slice();
    ids.forEach(id => {
      const idx = extras.indexOf(id);
      if (idx >= 0) extras.splice(idx, 1);
    });
    this.form.patchValue({ apiarioIdsExtras: extras });
  }
}