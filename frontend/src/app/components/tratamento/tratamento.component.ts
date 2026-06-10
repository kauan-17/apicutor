import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ApiarioService } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';
import { TratamentoService } from '../../services/tratamento.service';

@Component({
  selector: 'app-tratamento',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './tratamento.component.html',
  styleUrls: ['./tratamento.component.css']
})
export class TratamentoComponent implements OnInit {
  apiarios: any[] = [];
  colmeias: any[] = [];
  tratamentos: any[] = [];
  selecionadoApiarioId?: number;
  selecionadoColmeiaId?: number;
  loading = false;
  error?: string;
  sucesso?: string;

  form!: FormGroup;

  tiposTratamento = [
    { value: 'ACARICIDA', label: 'Acaricida' },
    { value: 'ANTIBIOTICO', label: 'Antibiótico' },
    { value: 'FUNGICIDA', label: 'Fungicida' },
    { value: 'VITAMINA', label: 'Vitamina' },
    { value: 'VERMIFUGO', label: 'Vermífugo' },
    { value: 'OXALICO', label: 'Ácido Oxálico' },
    { value: 'TIMOL', label: 'Timol' },
    { value: 'OUTRO', label: 'Outro' }
  ];

  constructor(
    private fb: FormBuilder,
    private apiarioService: ApiarioService,
    private colmeiaService: ColmeiaService,
    private tratamentoService: TratamentoService
  ) {
    this.form = this.fb.group({
      dataAplicacao: ['', Validators.required],
      tipoTratamento: ['', Validators.required],
      produto: ['', Validators.required],
      dose: [null],
      unidadeDose: [''],
      observacoes: ['']
    });
  }

  ngOnInit(): void {
    this.apiarioService.getApiarios().subscribe({
      next: (res) => { this.apiarios = res ?? []; },
      error: () => { this.error = 'Falha ao carregar apiários'; }
    });
  }

  carregarColmeias(): void {
    this.colmeias = [];
    this.tratamentos = [];
    this.selecionadoColmeiaId = undefined;
    if (!this.selecionadoApiarioId) return;
    this.loading = true;
    this.colmeiaService.getColmeiasByApiario(this.selecionadoApiarioId).subscribe({
      next: (res) => { this.colmeias = res ?? []; this.loading = false; },
      error: () => { this.error = 'Falha ao carregar colmeias'; this.loading = false; }
    });
  }

  carregarTratamentos(): void {
    if (!this.selecionadoColmeiaId) return;
    const id = Number(this.selecionadoColmeiaId);
    this.loading = true; this.error = undefined;
    this.tratamentoService.getByColmeia(id).subscribe({
      next: (res) => { this.tratamentos = res ?? []; this.loading = false; },
      error: (err) => { this.error = this.errorMsg(err, 'carregar tratamentos'); this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid || !this.selecionadoColmeiaId) return;
    const colmeiaId = Number(this.selecionadoColmeiaId);
    const payload = {
      colmeiaId,
      dataAplicacao: this.form.value.dataAplicacao,
      tipoTratamento: this.form.value.tipoTratamento,
      produto: this.form.value.produto,
      dose: this.form.value.dose ? Number(this.form.value.dose) : null,
      unidadeDose: this.form.value.unidadeDose,
      observacoes: this.form.value.observacoes
    };
    this.loading = true; this.error = undefined; this.sucesso = undefined;
    this.tratamentoService.create(payload).subscribe({
      next: () => {
        this.sucesso = 'Tratamento registrado com sucesso!';
        this.form.reset({ dataAplicacao: '', tipoTratamento: '', produto: '', dose: null, unidadeDose: '', observacoes: '' });
        this.loading = false;
        this.carregarTratamentos();
      },
      error: (err) => { this.error = this.errorMsg(err, 'salvar tratamento'); this.loading = false; }
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja excluir este tratamento?')) return;
    this.tratamentoService.delete(id).subscribe({
      next: () => { this.tratamentos = this.tratamentos.filter(t => t.id !== id); },
      error: (err) => { this.error = this.errorMsg(err, 'excluir tratamento'); }
    });
  }

  labelTipo(valor: string): string {
    return this.tiposTratamento.find(t => t.value === valor)?.label ?? valor;
  }

  private errorMsg(err: any, action: string): string {
    const status = err?.status;
    const backend = (typeof err?.error === 'string') ? err.error : (err?.error?.error || err?.message || '');
    if (status === 403) return `Sem permissão para ${action}.`;
    if (status === 401) return `Sessão expirada (401) ao ${action}.`;
    if (status) return `Falha (${status}) ao ${action}. ${backend || ''}`.trim();
    return `Falha ao ${action}. ${backend || ''}`.trim();
  }
}
