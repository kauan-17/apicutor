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
    const payload = {
      nome: this.form.value.nome,
      ativa: this.form.value.ativa,
      apiario: { id: this.form.value.apiarioId }
    };
    this.colmeiaService.createColmeia(payload).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/colmeias']); },
      error: () => { this.saving = false; this.error = 'Falha ao salvar'; }
    });
  }
}
