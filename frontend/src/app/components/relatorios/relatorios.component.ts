import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProducaoService } from '../../services/producao.service';
import { ApiarioService, Apiario } from '../../services/apiario.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-relatorios',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './relatorios.component.html',
  styleUrls: ['./relatorios.component.css']
})
export class RelatoriosComponent implements OnInit {
  apiarios: Apiario[] = [];
  selectedApiarioId: number | null = null;
  ano = new Date().getFullYear();
  mes = new Date().getMonth() + 1; // 1-12; 0 = Todos
  incluirMes = true; // controle para usar mês no filtro
  sortCampo: 'item' | 'kg' = 'kg';
  sortDirecao: 'asc' | 'desc' = 'desc';

  carregando = false;
  erro?: string;
  resultado: Array<{ item: string; kg: number }> = [];

  constructor(
    private producaoService: ProducaoService,
    private apiarioService: ApiarioService
  ) {}

  ngOnInit(): void {
    this.apiarioService.getApiarios().pipe(
      catchError(err => {
        console.warn('[Relatórios] Falha ao carregar apiários:', err);
        return of([] as Apiario[]);
      })
    ).subscribe(lista => {
      this.apiarios = lista ?? [];
    });
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.erro = undefined;
    this.resultado = [];
    const mesParam = this.incluirMes ? this.mes : undefined;

    const obs = this.selectedApiarioId
      ? this.producaoService.producaoMensal(this.selectedApiarioId, this.ano, mesParam)
      : this.producaoService.producaoMensalTotal(this.ano, mesParam);

    obs.pipe(
      catchError(err => {
        console.warn('[Relatórios] Falha ao carregar relatório:', err);
        this.erro = 'Falha ao carregar relatório. Tente novamente mais tarde.';
        return of({} as Record<string, number>);
      })
    ).subscribe((data: Record<string, number>) => {
      const entries = Object.entries(data || {});
      this.resultado = entries.map(([k, v]) => ({ item: k, kg: Number(v) || 0 }));
      this.ordenar();
      this.carregando = false;
    });
  }

  totalKg(): number {
    return this.resultado.reduce((sum, r) => sum + (Number.isFinite(r.kg) ? r.kg : 0), 0);
  }

  downloadCsv(): void {
    const header = 'Item,Kg\n';
    const rows = this.resultado.map(r => `${this.escapeCsv(r.item)},${r.kg}`);
    const csv = header + rows.join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    const nomeApiario = this.selectedApiarioId
      ? (this.apiarios.find(a => a.id === this.selectedApiarioId)?.nome || `apiario-${this.selectedApiarioId}`)
      : 'total';
    const mesParte = this.incluirMes ? `-${String(this.mes).padStart(2, '0')}` : '';
    link.download = `relatorio-producao-${nomeApiario}-${this.ano}${mesParte}.csv`;
    link.click();
    setTimeout(() => URL.revokeObjectURL(url), 5000);
  }

  private escapeCsv(value: string): string {
    if (value == null) return '';
    const needsQuotes = /[",\n]/.test(value);
    let v = value.replace(/"/g, '""');
    return needsQuotes ? `"${v}"` : v;
  }

  atualizarOrdenacao(): void {
    this.ordenar();
  }

  private ordenar(): void {
    const dir = this.sortDirecao === 'asc' ? 1 : -1;
    this.resultado.sort((a, b) => {
      if (this.sortCampo === 'kg') {
        return (a.kg - b.kg) * dir;
      }
      return a.item.localeCompare(b.item) * dir;
    });
  }

  limparFiltros(): void {
    this.selectedApiarioId = null;
    this.incluirMes = false;
    this.mes = new Date().getMonth() + 1;
    this.ano = new Date().getFullYear();
    this.carregar();
  }

  resumoFiltros(): string {
    const nomeApiario = this.selectedApiarioId
      ? (this.apiarios.find(a => a.id === this.selectedApiarioId)?.nome || `Apiário ${this.selectedApiarioId}`)
      : 'Agregado';
    const mesParte = this.incluirMes ? this.nomeMes(this.mes) : 'Todos os meses';
    return `${nomeApiario} • ${this.ano} • ${mesParte}`;
  }

  private nomeMes(m: number): string {
    const nomes = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
    return nomes[(m - 1) % 12] || '';
  }
}
