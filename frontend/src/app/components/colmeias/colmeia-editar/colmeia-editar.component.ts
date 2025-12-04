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
        this.form.patchValue({
          nome: c?.nome,
          ativa: c?.ativa,
          apiarioId: c?.apiario?.id ?? null
        });
        this.loading = false;
      },
      error: () => { this.error = 'Falha ao carregar colmeia'; this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.saving = true; this.error = undefined;
    const payload = {
      nome: this.form.value.nome,
      ativa: this.form.value.ativa,
      apiario: { id: this.form.value.apiarioId }
    };
    this.colmeiaService.updateColmeia(this.id, payload).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/colmeias']); },
      error: () => { this.saving = false; this.error = 'Falha ao salvar'; }
    });
  }
}
