import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';
import { ColmeiaService } from '../../../services/colmeia.service';

@Component({
  selector: 'app-colmeia-editar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './colmeia-editar.component.html',
  styleUrls: []
})
export class ColmeiaEditarComponent implements OnInit {
  id!: number;
  form!: FormGroup;
  apiarios: any[] = [];
  loading = true;
  saving = false;
  error?: string;
  private colmeiaAtual: any;

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private apiarioService: ApiarioService, private colmeiaService: ColmeiaService) {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      apiarioId: [null as number | null, Validators.required],
      ativa: [true]
    });
  }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.apiarioService.getApiarios().subscribe({
      next: (res) => { this.apiarios = res ?? []; this.load(); },
      error: () => { this.error = 'Falha ao carregar apiários'; this.loading = false; }
    });
  }

  load(): void {
    this.colmeiaService.getColmeia(this.id).subscribe({
      next: (c) => {
        this.colmeiaAtual = c;
        this.form.patchValue({
          nome: c?.identificacao,
          ativa: c?.status ? c.status !== 'INATIVA' : true,
          apiarioId: c?.apiarioId ?? null
        });
        this.loading = false;
      },
      error: () => { this.error = 'Falha ao carregar colmeia'; this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.saving = true; this.error = undefined;
    const atual = this.colmeiaAtual || {};
    const nome = (this.form.value.nome || '').toString();
    const ativa = !!this.form.value.ativa;
    const dataInstalacao = atual.dataInstalacao ? atual.dataInstalacao.toString().substring(0, 10) : new Date().toISOString().substring(0, 10);
    const quantidadeMelgueiras = Number(
      atual.quantidadeMelgueiras ?? (atual.melgueira ? 1 : 0)
    );
    const qtd = Number.isFinite(quantidadeMelgueiras) && quantidadeMelgueiras > 0 ? quantidadeMelgueiras : 0;
    const payload = {
      apiarioId: this.form.value.apiarioId,
      identificacao: nome,
      tipo: atual.tipo ?? 'LANGSTROTH',
      tipoAbelha: atual.tipoAbelha ?? 'EUROPEIA',
      rainhaStatus: atual.rainhaStatus ?? 'NOVA',
      origemColonia: atual.origemColonia ?? 'CAPTURA',
      quantidadeMelgueiras: qtd,
      melgueira: qtd > 0,
      dataInstalacao,
      status: ativa ? 'ATIVA' : 'INATIVA',
      observacoes: atual.observacoes ?? ''
    };
    this.colmeiaService.updateColmeia(this.id, payload).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/colmeias']); },
      error: (err) => { this.saving = false; this.error = this.errorMsg(err, 'atualizar colmeia'); }
    });
  }

  private errorMsg(err: any, action: string): string {
    const status = err?.status;
    const backend =
      (typeof err?.error === 'string')
        ? err.error
        : (err?.error?.error || err?.error?.message || err?.message || '');
    if (status === 400) return `Campos inválidos (400) ao ${action}. ${backend || ''}`.trim();
    if (status === 401) return `Sessão expirada ou sem autorização (401) ao ${action}.`;
    if (status === 403) return `Sem permissão (403) para ${action}. Verifique acesso ao apiário.`;
    if (status === 404) return `Registro não encontrado (404) ao ${action}.`;
    if (status) return `Falha (${status}) ao ${action}. ${backend || ''}`.trim();
    return `Falha ao ${action}. ${backend || ''}`.trim();
  }
}
