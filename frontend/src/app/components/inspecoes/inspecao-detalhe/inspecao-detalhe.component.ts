import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { InspecaoService } from '../../../services/inspecao.service';

@Component({
  selector: 'app-inspecao-detalhe',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './inspecao-detalhe.component.html',
  styleUrls: []
})
export class InspecaoDetalheComponent implements OnInit {
  id!: number;
  loading = true;
  saving = false;
  error?: string;
  form!: FormGroup;

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private service: InspecaoService) {
    this.form = this.fb.group({
      dataHora: [''],
      quadrosComCria: [0, [Validators.min(0)]],
      quadrosComMel: [0, [Validators.min(0)]],
      quadrosComPolen: [0, [Validators.min(0)]],
      observacoes: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.id = Number(params.get('id'));
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.service.getById(this.id).subscribe({
      next: (res) => {
        this.form.patchValue({
          dataHora: res?.dataHora,
          quadrosComCria: res?.quadrosComCria,
          quadrosComMel: res?.quadrosComMel,
          quadrosComPolen: res?.quadrosComPolen,
          observacoes: res?.observacoes,
        });
        this.loading = false;
      },
      error: () => { this.error = 'Falha ao carregar'; this.loading = false; }
    });
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.saving = true; this.error = undefined;
    this.service.update(this.id, this.form.value).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/inspecoes']); },
      error: () => { this.error = 'Falha ao salvar'; this.saving = false; }
    });
  }

  excluir(): void {
    if (!confirm('Excluir esta inspeção?')) return;
    this.saving = true; this.error = undefined;
    this.service.delete(this.id).subscribe({
      next: () => { this.saving = false; this.router.navigate(['/inspecoes']); },
      error: () => { this.error = 'Falha ao excluir'; this.saving = false; }
    });
  }
}
