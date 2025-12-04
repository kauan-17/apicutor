import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ApiarioService } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';
import { ProducaoService } from '../../services/producao.service';

@Component({
  selector: 'app-producao',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './producao.component.html',
  styleUrls: ['./producao.component.css']
})
export class ProducaoComponent implements OnInit {
  apiarios: any[] = [];
  colmeias: any[] = [];
  producoes: any[] = [];
  selecionadoApiarioId?: number;
  selecionadoColmeiaId?: number;
  loading = false;
  error?: string;

  form!: FormGroup;
  tiposProduto = [
    { value: 'MEL', label: 'Mel' },
    { value: 'POLEN', label: 'Pólen' },
    { value: 'PROPOLIS', label: 'Própolis' },
    { value: 'GELEIA_REAL', label: 'Geleia Real' },
    { value: 'CERA', label: 'Cera' },
    { value: 'OUTRO', label: 'Outro' }
  ];
  unidadesMedida = [
    { value: 'KG', label: 'Kg' },
    { value: 'G', label: 'g' },
    { value: 'L', label: 'L' },
    { value: 'ML', label: 'ml' }
  ];

  constructor(private fb: FormBuilder, private apiarioService: ApiarioService, private colmeiaService: ColmeiaService, private producaoService: ProducaoService) {
    this.form = this.fb.group({
      dataColheita: ['', Validators.required],
      tipoProduto: ['', Validators.required],
      quantidade: [0, [Validators.required, Validators.min(0.01)]],
      unidadeMedida: ['', Validators.required],
      lote: [''],
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
    if (!this.selecionadoApiarioId) return;
    this.loading = true; this.error = undefined;
    this.colmeiaService.getColmeiasByApiario(this.selecionadoApiarioId).subscribe({
      next: (res) => { this.colmeias = res ?? []; this.loading = false; },
      error: () => { this.error = 'Falha ao carregar colmeias'; this.loading = false; }
    });
  }

  carregarProducao(): void {
    if (!this.selecionadoColmeiaId) return;
    const id = Number(this.selecionadoColmeiaId);
    if (!Number.isFinite(id)) { this.error = 'Colmeia inválida'; return; }
    this.loading = true; this.error = undefined;
    this.producaoService.getByColmeia(id).subscribe({
      next: (res) => { this.producoes = res ?? []; this.loading = false; },
      error: (err) => { this.error = this.errorMsg(err, 'carregar produção'); this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid || !this.selecionadoColmeiaId) return;
    const payload = {
      colmeia: { id: this.selecionadoColmeiaId },
      ...this.form.value
    };
    this.loading = true; this.error = undefined;
    this.producaoService.create(payload).subscribe({
      next: () => { this.form.reset(); this.loading = false; this.carregarProducao(); },
      error: (err) => { this.error = this.errorMsg(err, 'salvar produção'); this.loading = false; }
    });
  }

  private errorMsg(err: any, action: string): string {
    const status = err?.status;
    const backend = (typeof err?.error === 'string') ? err.error : (err?.error?.error || err?.message || '');
    if (status === 401) return `Sessão expirada ou sem autorização (401) ao ${action}.`;
    if (status === 403) return `Sem permissão (403) para ${action}. Verifique acesso ao apiário/colmeia.`;
    if (status === 404) return `Registro não encontrado (404) ao ${action}.`;
    if (status) return `Falha (${status}) ao ${action}. ${backend || ''}`.trim();
    return `Falha ao ${action}. ${backend || ''}`.trim();
  }
}
