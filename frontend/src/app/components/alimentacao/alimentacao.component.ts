import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ApiarioService } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';
import { AlimentacaoService } from '../../services/alimentacao.service';

@Component({
  selector: 'app-alimentacao',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './alimentacao.component.html',
  styleUrls: ['./alimentacao.component.css']
})
export class AlimentacaoComponent implements OnInit {
  apiarios: any[] = [];
  colmeias: any[] = [];
  alimentacoes: any[] = [];
  selecionadoApiarioId?: number;
  selecionadoColmeiaId?: number;
  loading = false;
  error?: string;
  sucesso?: string;

  form!: FormGroup;

  tiposAlimento = [
    { value: 'XAROPE_ACUCAR', label: 'Xarope de Açúcar' },
    { value: 'XAROPE_MEL', label: 'Xarope de Mel' },
    { value: 'XAROPE_INVERTIDO', label: 'Xarope Invertido' },
    { value: 'CANDI', label: 'Candi' },
    { value: 'PROTEICO', label: 'Proteico' },
    { value: 'PASTA_PROTEICA', label: 'Pasta Proteica' },
    { value: 'OUTRO', label: 'Outro' }
  ];

  unidades = [
    { value: 'KG', label: 'Kg' },
    { value: 'G', label: 'g' },
    { value: 'L', label: 'L' },
    { value: 'ML', label: 'ml' }
  ];

  constructor(
    private fb: FormBuilder,
    private apiarioService: ApiarioService,
    private colmeiaService: ColmeiaService,
    private alimentacaoService: AlimentacaoService
  ) {
    this.form = this.fb.group({
      dataAplicacao: ['', Validators.required],
      tipoAlimento: ['', Validators.required],
      quantidade: [null, [Validators.required, Validators.min(0.01)]],
      unidade: ['', Validators.required],
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
    this.alimentacoes = [];
    this.selecionadoColmeiaId = undefined;
    if (!this.selecionadoApiarioId) return;
    this.loading = true;
    this.colmeiaService.getColmeiasByApiario(this.selecionadoApiarioId).subscribe({
      next: (res) => { this.colmeias = res ?? []; this.loading = false; },
      error: () => { this.error = 'Falha ao carregar colmeias'; this.loading = false; }
    });
  }

  carregarAlimentacoes(): void {
    if (!this.selecionadoColmeiaId) return;
    const id = Number(this.selecionadoColmeiaId);
    this.loading = true; this.error = undefined;
    this.alimentacaoService.getByColmeia(id).subscribe({
      next: (res) => { this.alimentacoes = res ?? []; this.loading = false; },
      error: (err) => { this.error = this.errorMsg(err, 'carregar alimentações'); this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid || !this.selecionadoColmeiaId) return;
    const colmeiaId = Number(this.selecionadoColmeiaId);
    const payload = {
      colmeiaId,
      dataAplicacao: this.form.value.dataAplicacao,
      tipoAlimento: this.form.value.tipoAlimento,
      quantidade: Number(this.form.value.quantidade),
      unidade: this.form.value.unidade,
      observacoes: this.form.value.observacoes
    };
    this.loading = true; this.error = undefined; this.sucesso = undefined;
    this.alimentacaoService.create(payload).subscribe({
      next: () => {
        this.sucesso = 'Alimentação registrada com sucesso!';
        this.form.reset({ dataAplicacao: '', tipoAlimento: '', quantidade: null, unidade: '', observacoes: '' });
        this.loading = false;
        this.carregarAlimentacoes();
      },
      error: (err) => { this.error = this.errorMsg(err, 'salvar alimentação'); this.loading = false; }
    });
  }

  excluir(id: number): void {
    if (!confirm('Deseja excluir este registro?')) return;
    this.alimentacaoService.delete(id).subscribe({
      next: () => { this.alimentacoes = this.alimentacoes.filter(a => a.id !== id); },
      error: (err) => { this.error = this.errorMsg(err, 'excluir alimentação'); }
    });
  }

  labelTipo(valor: string): string {
    return this.tiposAlimento.find(t => t.value === valor)?.label ?? valor;
  }

  labelUnidade(valor: string): string {
    return this.unidades.find(u => u.value === valor)?.label ?? valor;
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
