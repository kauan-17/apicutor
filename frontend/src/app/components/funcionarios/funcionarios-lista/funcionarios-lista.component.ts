import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiarioService } from '../../../services/apiario.service';
import { FuncionarioService } from '../../../services/funcionario.service';

@Component({
  selector: 'app-funcionarios-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './funcionarios-lista.component.html',
  styleUrls: []
})
export class FuncionariosListaComponent implements OnInit {
  apiarios: any[] = [];
  funcionarios: any[] = [];
  selecionadoApiarioId?: number;
  loading = false;
  error?: string;
  realocarPara: Record<number, number | undefined> = {};

  constructor(private apiarioService: ApiarioService, private funcionarioService: FuncionarioService) {}

  ngOnInit(): void {
    this.apiarioService.getApiarios().subscribe({
      next: (res) => { this.apiarios = res ?? []; },
      error: () => { this.error = 'Falha ao carregar apiários'; }
    });
  }

  carregarFuncionarios(): void {
    if (!this.selecionadoApiarioId) return;
    this.loading = true; this.error = undefined;
    this.funcionarioService.listByApiario(this.selecionadoApiarioId).subscribe({
      next: (res: any) => { this.funcionarios = res ?? []; this.loading = false; },
      error: () => { this.error = 'Falha ao carregar funcionários'; this.loading = false; }
    });
  }

  realocar(funcionarioId: number, deApiarioId: number): void {
    const paraApiarioId = this.realocarPara[funcionarioId];
    if (!paraApiarioId || paraApiarioId === deApiarioId) return;
    this.loading = true; this.error = undefined;
    this.funcionarioService.atribuir(funcionarioId, paraApiarioId).subscribe({
      next: () => { this.loading = false; this.carregarFuncionarios(); },
      error: () => { this.error = 'Falha ao realocar funcionário'; this.loading = false; }
    });
  }
}
