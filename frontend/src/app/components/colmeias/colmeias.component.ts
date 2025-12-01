import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ColmeiaService } from '../../services/colmeia.service';
import { ApiarioService, Apiario } from '../../services/apiario.service';
import { RoleVisibilityDirective } from '../../auth/role-visibility.directive';

@Component({
  selector: 'app-colmeias',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, RoleVisibilityDirective],
  templateUrl: './colmeias.component.html',
  styleUrls: ['./colmeias.component.css']
})
export class ColmeiasComponent implements OnInit {
  apiarios: Apiario[] = [];
  colmeias: any[] = [];
  selectedApiarioId: number | null = null;
  loading = false;
  error: string | null = null;
  showForm = false;
  editingColmeiaId: number | null = null;

  tipos = [
    { value: 'LANGSTROTH', label: 'Langstroth' },
    { value: 'DADANT', label: 'Dadant' },
    { value: 'SCHENK', label: 'Schenk' },
    { value: 'WARRE', label: 'Warré' },
    { value: 'QUENIANA', label: 'Queniana' },
    { value: 'OUTRO', label: 'Outro' }
  ];

  tiposAbelha = [
    { value: 'AFRICANA', label: 'Africana' },
    { value: 'EUROPEIA', label: 'Europeia' },
    { value: 'CARNICA', label: 'Cárnica' },
    { value: 'ITALIANA', label: 'Italiana' },
    { value: 'MISTA', label: 'Mista' },
    { value: 'OUTRA', label: 'Outra' }
  ];

  rainhaStatuses = [
    { value: 'NOVA', label: 'Nova' },
    { value: 'ANTIGA', label: 'Antiga' }
  ];

  origensColonia = [
    { value: 'CAPTURA', label: 'Captura' },
    { value: 'DIVISAO', label: 'Divisão' }
  ];

  statuses = [
    { value: 'ATIVA', label: 'Ativa' },
    { value: 'EM_OBSERVACAO', label: 'Em observação' },
    { value: 'INATIVA', label: 'Inativa' },
    { value: 'DOENTE', label: 'Doente' },
    { value: 'PERDIDA', label: 'Perdida' }
  ];

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private colmeiaService: ColmeiaService,
    private apiarioService: ApiarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      identificacao: ['', [Validators.required, Validators.maxLength(50)]],
      tipo: ['LANGSTROTH', [Validators.required]],
      tipoAbelha: ['EUROPEIA', [Validators.required]],
      rainhaStatus: ['NOVA', [Validators.required]],
      origemColonia: ['CAPTURA', [Validators.required]],
      quantidadeMelgueiras: [0, [Validators.min(0)]],
      dataInstalacao: ['', [Validators.required]],
      status: ['ATIVA', [Validators.required]],
      observacoes: ['']
    });
  }

  ngOnInit(): void {
    const qpId = this.route.snapshot.queryParamMap.get('apiarioId');
    const parsed = qpId ? Number(qpId) : null;
    this.selectedApiarioId = Number.isFinite(parsed as number) ? parsed : null;
    this.loadApiarios();
  }

  tipoLabel(value: string | null | undefined): string {
    const v = (value || '').toString();
    const opt = this.tipos.find(t => t.value === v);
    return opt ? opt.label : v;
  }

  tipoAbelhaLabel(value: string | null | undefined): string {
    const v = (value || '').toString();
    const opt = this.tiposAbelha.find(t => t.value === v);
    return opt ? opt.label : v;
  }

  origemColoniaLabel(value: string | null | undefined): string {
    const v = (value || '').toString();
    const opt = this.origensColonia.find(t => t.value === v);
    return opt ? opt.label : v;
  }

  getQuantidadeMelgueiras(c: any): number {
    if (!c) return 0;
    const raw = c.quantidadeMelgueiras ?? (c.melgueira ? 1 : 0);
    const n = Number(raw);
    return Number.isFinite(n) && n >= 0 ? n : 0;
  }

  loadApiarios(): void {
    this.apiarioService.getApiarios().subscribe({
      next: (items) => {
        this.apiarios = items || [];
        if (!this.selectedApiarioId && this.apiarios.length > 0) {
          this.selectedApiarioId = this.apiarios[0].id;
        }
        this.loadColmeias();
      },
      error: (err) => {
        this.error = 'Não foi possível carregar apiários. Verifique o backend.';
        console.error('Erro ao carregar apiários', err);
      }
    });
  }

  onApiarioChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedApiarioId = target.value ? Number(target.value) : null;
    this.loadColmeias();
  }

  loadColmeias(): void {
    this.loading = true;
    this.error = null;
    const src$ = this.selectedApiarioId
      ? this.colmeiaService.getColmeiasByApiario(this.selectedApiarioId)
      : this.colmeiaService.getColmeias();
    src$.subscribe({
      next: (items) => {
        this.colmeias = items || [];
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Não foi possível carregar colmeias. Verifique o backend.';
        console.error('Erro ao carregar colmeias', err);
      }
    });
  }

  toggleForm(): void {
    // Abrir/fechar formulário. Se fechar, limpar modo edição
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.cancelEdit();
    }
  }

  submit(): void {
    if (!this.selectedApiarioId) {
      this.error = 'Selecione um apiário para cadastrar a colmeia.';
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      identificacao: this.form.value.identificacao,
      tipo: this.form.value.tipo,
      tipoAbelha: this.form.value.tipoAbelha,
      rainhaStatus: this.form.value.rainhaStatus,
      origemColonia: this.form.value.origemColonia,
      quantidadeMelgueiras: Number(this.form.value.quantidadeMelgueiras) || 0,
      melgueira: Number(this.form.value.quantidadeMelgueiras) > 0,
      dataInstalacao: this.form.value.dataInstalacao,
      status: this.form.value.status,
      observacoes: this.form.value.observacoes,
      apiario: { id: this.selectedApiarioId }
    };

    this.loading = true;
    const isEdit = !!this.editingColmeiaId;
    const obs$ = isEdit
      ? this.colmeiaService.updateColmeia(this.editingColmeiaId!, payload)
      : this.colmeiaService.createColmeia(payload);

    obs$.subscribe({
      next: () => {
        this.loading = false;
        this.showForm = false;
        this.cancelEdit();
        this.form.reset({
          tipo: 'LANGSTROTH',
          tipoAbelha: 'EUROPEIA',
          rainhaStatus: 'NOVA',
          origemColonia: 'CAPTURA',
          quantidadeMelgueiras: 0,
          status: 'ATIVA'
        });
        this.loadColmeias();
      },
      error: (err) => {
        this.loading = false;
        this.error = isEdit
          ? 'Falha ao atualizar colmeia. Verifique o backend e os campos.'
          : 'Falha ao criar colmeia. Verifique o backend e os campos.';
        console.error(isEdit ? 'Erro ao atualizar colmeia' : 'Erro ao criar colmeia', err);
      }
    });
  }

  startEdit(c: any): void {
    this.editingColmeiaId = c?.id || null;
    if (!this.editingColmeiaId) return;
    this.showForm = true;
    this.form.patchValue({
      identificacao: c.identificacao || '',
      tipo: c.tipo || 'LANGSTROTH',
      tipoAbelha: c.tipoAbelha || 'EUROPEIA',
      rainhaStatus: c.rainhaStatus || 'NOVA',
      origemColonia: c.origemColonia || 'CAPTURA',
      quantidadeMelgueiras: (c as any).quantidadeMelgueiras ?? (c.melgueira ? 1 : 0),
      dataInstalacao: c.dataInstalacao ? (c.dataInstalacao.toString().substring(0, 10)) : '',
      status: c.status || 'ATIVA',
      observacoes: c.observacoes || ''
    });
  }

  cancelEdit(): void {
    this.editingColmeiaId = null;
  }

  deleteColmeia(c: any): void {
    const id = c?.id;
    if (!id) return;
    const confirmar = window.confirm('Tem certeza que deseja excluir esta colmeia? Esta ação não pode ser desfeita.');
    if (!confirmar) return;
    this.loading = true;
    this.colmeiaService.deleteColmeia(id).subscribe({
      next: () => {
        this.loading = false;
        if (this.editingColmeiaId === id) {
          this.cancelEdit();
          this.showForm = false;
        }
        this.loadColmeias();
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Falha ao excluir colmeia. Verifique permissões.';
        console.error('Erro ao excluir colmeia', err);
      }
    });
  }
  navigateBack(): void {
    const id = this.selectedApiarioId;
    if (id) {
      this.router.navigate(['/apiarios', id]);
    } else {
      this.router.navigate(['/apiarios']);
    }
  }
}
