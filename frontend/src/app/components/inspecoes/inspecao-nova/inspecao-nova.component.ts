import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ColmeiaService } from '../../../services/colmeia.service';
import { InspecaoService } from '../../../services/inspecao.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-inspecao-nova',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './inspecao-nova.component.html',
  styleUrls: ['./inspecao-nova.component.css']
})
export class InspecaoNovaComponent {
  apiarioId!: number;
  responsavel = '';
  data = new Date().toISOString().substring(0, 10);
  observacoes = '';
  colmeiasVerificadas = 0;
  colmeias: any[] = [];
  selecionadas: number[] = [];
  observacaoPorColmeia: Record<number, string> = {};
  feedback?: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private inspecaoService: InspecaoService,
    private colmeiaService: ColmeiaService
  ) {
    this.route.params.subscribe(p => {
      const id = +p['id'];
      this.apiarioId = id || 1;
      this.carregarColmeias();
    });
  }

  carregarColmeias(): void {
    this.colmeiaService.getColmeiasByApiario(this.apiarioId).subscribe({
      next: (items) => { this.colmeias = items || []; },
      error: () => { this.colmeias = []; }
    });
  }

  toggleSelecionada(id: number, checked: boolean): void {
    const set = new Set<number>(this.selecionadas);
    if (checked) set.add(id); else set.delete(id);
    this.selecionadas = Array.from(set);
    this.colmeiasVerificadas = this.selecionadas.length;
  }

  salvar() {
    if (!this.selecionadas.length) {
      this.feedback = 'Selecione ao menos uma colmeia.';
      setTimeout(() => (this.feedback = undefined), 3000);
      return;
    }

    const dataHora = this.data ? `${this.data}T00:00:00` : undefined;
    const requests = this.selecionadas.map(colmeiaId => {
      const observacoesPorColmeia = this.observacaoPorColmeia[colmeiaId];
      const observacoesFinal = [this.observacoes, observacoesPorColmeia]
        .filter(v => typeof v === 'string' && v.trim())
        .join('\n');

      return this.inspecaoService.create({
        colmeiaId,
        dataHora,
        observacoes: observacoesFinal || undefined
      });
    });

    forkJoin(requests).subscribe({
      next: () => {
        this.feedback = 'Inspeção registrada com sucesso.';
        setTimeout(() => this.router.navigate(['/apiarios', this.apiarioId]), 600);
      },
      error: () => {
        this.feedback = 'Falha ao registrar inspeção.';
        setTimeout(() => (this.feedback = undefined), 3000);
      }
    });
  }

  cancelar() {
    this.router.navigate(['/apiarios', this.apiarioId]);
  }
}
