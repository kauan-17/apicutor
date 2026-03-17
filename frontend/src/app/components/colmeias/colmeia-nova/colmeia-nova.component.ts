import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiarioService } from '../../../services/apiario.service';
import { ColmeiaService } from '../../../services/colmeia.service';

@Component({
  selector: 'app-colmeia-nova',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './colmeia-nova.component.html',
  styleUrls: []
})
export class ColmeiaNovaComponent implements OnInit {
  form!: FormGroup;
  apiarios: any[] = [];
  loading = true;
  saving = false;
  error?: string;

  constructor(private fb: FormBuilder, private apiarioService: ApiarioService, private colmeiaService: ColmeiaService, private router: Router) {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      apiarioId: [null as number | null, Validators.required],
      ativa: [true]
    });
  }

  ngOnInit(): void {
    this.apiarioService.getApiarios().subscribe({
      next: (res) => { this.apiarios = res ?? []; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Falha ao carregar apiários'; }
    });
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.saving = true; this.error = undefined;
    const hoje = new Date().toISOString().substring(0, 10);
    const ativa = !!this.form.value.ativa;
    const payload = {
      apiarioId: this.form.value.apiarioId,
      identificacao: this.form.value.nome,
      tipo: 'LANGSTROTH',
      tipoAbelha: 'EUROPEIA',
      rainhaStatus: 'NOVA',
      origemColonia: 'CAPTURA',
      quantidadeMelgueiras: 0,
      melgueira: false,
      dataInstalacao: hoje,
      status: ativa ? 'ATIVA' : 'INATIVA',
      observacoes: ''
    };
    this.colmeiaService.createColmeia(payload).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/colmeias']); },
      error: (err) => { this.saving = false; this.error = this.errorMsg(err, 'criar colmeia'); }
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
