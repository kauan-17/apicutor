import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ApiarioService } from '../../services/apiario.service';
import { InsumoService } from '../../services/insumo.service';

@Component({
  selector: 'app-insumos',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './insumos.component.html',
  styleUrls: ['./insumos.component.css']
})
export class InsumosComponent implements OnInit {
  apiarios: any[] = [];
  insumos: any[] = [];
  selecionadoApiarioId?: number;
  loading = false;
  error?: string;
  sucesso?: string;

  form!: FormGroup;

  tiposInsumo = [
    { value: 'CAIXA', label: 'Caixa' },
    { value: 'CERA_ALVEOLADA', label: 'Cera Alveolada' },
    { value: 'EPI', label: 'EPI (Equipamento de Proteção)' },
    { value: 'MEDICAMENTO', label: 'Medicamento' },
    { value: 'FUMIGADOR', label: 'Fumigador' },
    { value: 'EXTRATOR', label: 'Extrator' },
    { value: 'QUADRO', label: 'Quadro' },
    { value: 'OUTRO', label: 'Outro' }
  ];

  tiposMovimento = [
    { value: 'ENTRADA', label: 'Entrada' },
    { value: 'SAIDA', label: 'Saída' }
  ];

  constructor(
    private fb: FormBuilder,
    private apiarioService: ApiarioService,
    private insumoService: InsumoService
  ) {
    this.form = this.fb.group({
      tipoInsumo: ['', Validators.required],
      descricao: [''],
      quantidade: [null, [Validators.required, Validators.min(0.01)]],
      unidade: ['', Validators.required],
      tipoMovimento: ['', Validators.required],
      dataMovimento: ['', Validators.required],
      observacoes: ['']
    });
  }

  ngOnInit(): void {
    this.apiarioService.getApiarios().subscribe({
      next: (res) => { this.apiarios = res ?? []; },
      error: () => { this.error = 'Falha ao carregar apiários'; }
    });
  }

  carregarInsumos(): void {
    this.insumos = [];
    if (!this.selecionadoApiarioId) return;
    this.loading = true; this.error = undefined;
    this.insumoService.getByApiario(this.selecionadoApiarioId).subscribe({
      next: (res) => { this.insumos = res ?? []; this.loading = false; },
      error: (err) => { this.error = this.errorMsg(err, 'carregar insumos'); this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid || !this.selecionadoApiarioId) return;
    const apiarioId = Number(this.selecionadoApiarioId);
    const payload = {
      apiarioId,
      tipoInsumo: this.form.value.tipoInsumo,
      descricao: this.form.value.descricao,
      quantidade: Number(this.form.value.quantidade),
      unidade: this.form.value.unidade,
      tipoMovimento: this.form.value.tipoMovimento,
      dataMovimento: this.form.value.dataMovimento,
      observacoes: this.form.value.observacoes
    };
    this.loading = true; this.error = undefined; this.sucesso = undefined;
    this.insumoService.create(payload).subscribe({
      next: () => {
        this.sucesso = 'Insumo registrado com sucesso!';
        this.form.reset({ tipoInsumo: '', descricao: '', quantidade: null, unidade: '', tipoMovimento: '', dataMovimento: '', observacoes: '' });
        this.loading = false;
        this.carregarInsumos();
      },
      error: (err) => { this.error = this.errorMsg(err, 'salvar insumo'); this.loading = false; }
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja excluir este registro?')) return;
    this.insumoService.delete(id).subscribe({
      next: () => { this.insumos = this.insumos.filter(i => i.id !== id); },
      error: (err) => { this.error = this.errorMsg(err, 'excluir insumo'); }
    });
  }

  labelTipo(valor: string): string {
    return this.tiposInsumo.find(t => t.value === valor)?.label ?? valor;
  }

  labelMovimento(valor: string): string {
    return this.tiposMovimento.find(t => t.value === valor)?.label ?? valor;
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
