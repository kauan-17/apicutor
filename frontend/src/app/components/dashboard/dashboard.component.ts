import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { ApiarioService, Apiario } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';
import { ProducaoService } from '../../services/producao.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  stats = {
    totalApiarios: 0,
    totalColmeias: 0,
    producaoMes: 0,
    alertasPendentes: 0
  };
  
  loading = true;
  erro?: string;
  userName = '';
  monthLabel?: string;
  monthYear?: number;

  constructor(
    private authService: AuthService,
    private apiarioService: ApiarioService,
    private colmeiaService: ColmeiaService,
    private producaoService: ProducaoService
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getCurrentUser()?.username || 'Usuário';
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.erro = undefined;
    const token = this.authService.getToken();
    if (!token) {
      this.loading = false;
      this.erro = 'Sessão não autenticada (401). Faça login para ver o dashboard.';
      console.warn('[Dashboard] Bloqueado sem token: não chamando backend.');
      return;
    }
    
    // Carregar dados do dashboard
    this.apiarioService.getApiarios().subscribe({
      next: (apiarios: Apiario[]) => {
        this.stats.totalApiarios = apiarios.length;
        
        // Contar colmeias de todos os apiários
        let totalColmeias = 0;
        apiarios.forEach((apiario: Apiario) => {
          totalColmeias += apiario.colmeias?.length || 0;
        });
        this.stats.totalColmeias = totalColmeias;
        
        // Produção real do mês atual (soma de todos os apiários)
        this.loadMonthlyProduction(apiarios);
        
        // Alertas: ainda sem backend, mantém simulação leve
        this.stats.alertasPendentes = Math.floor(Math.random() * 5);
        
        // loading será finalizado em loadMonthlyProduction
      },
      error: (error: any) => {
        console.error('Erro ao carregar dados do dashboard:', this.formatError(error));
        this.erro = this.formatError(error);
        this.loading = false;
      }
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }

  private formatError(err: any): string {
    const status = err?.status;
    const backendMsg = (typeof err?.error === 'string') ? err.error : (err?.error?.error || err?.message || '');
    if (status === 401) return 'Sessão expirada ou não autenticado (401). Faça login.';
    if (status === 403) return 'Acesso negado (403). Permissões insuficientes.';
    if (status === 404) return 'Recurso não encontrado (404).';
    if (status === 0) return 'Falha de conexão com o servidor. Verifique se o backend está ativo.';
    return `Falha ao carregar dados${status ? ` (HTTP ${status})` : ''}${backendMsg ? `: ${backendMsg}` : ''}`;
  }

  private loadMonthlyProduction(apiarios: Apiario[]): void {
    const year = new Date().getFullYear();
    const currentMonthIndex = new Date().getMonth();

    if (!apiarios || apiarios.length === 0) {
      this.stats.producaoMes = 0;
      this.loading = false;
      return;
    }

    const validApiarios = apiarios.filter(a => a && a.id != null);
    if (validApiarios.length === 0) {
      console.warn('[Dashboard] Nenhum apiário com id válido para produção mensal.');
      this.stats.producaoMes = 0;
      this.loading = false;
      return;
    }

    const requests = validApiarios.map(a =>
      this.producaoService.producaoMensal(a.id, year).pipe(
        catchError(err => {
          console.warn(`[Dashboard] Produção mensal ignorada para apiário ${a.id}: ${this.formatError(err)}`);
          return of({});
        })
      )
    );
    forkJoin(requests).subscribe({
      next: (results: Record<string, number>[]) => {
        const perApiario = results.map((map, i) => {
          const val = this.extractMonthValue(map, currentMonthIndex);
          const id = validApiarios[i]?.id;
          console.debug(`[Dashboard] Apiário ${id} produção no mês atual:`, val, map);
          return val;
        });
        let total = perApiario.reduce((sum, v) => sum + v, 0);
        if (total > 0) {
          this.monthLabel = this.ptMonthName(currentMonthIndex);
          this.monthYear = year;
        } else {
          // Fallback: somar o último mês do ano com produção positiva
          const monthTotals = new Array(12).fill(0);
          results.forEach(map => {
            for (let i = 0; i < 12; i++) {
              monthTotals[i] += this.extractMonthValue(map, i);
            }
          });
          let lastIndex = -1;
          for (let i = 11; i >= 0; i--) {
            if (monthTotals[i] > 0) { lastIndex = i; break; }
          }
          if (lastIndex >= 0) {
            total = monthTotals[lastIndex];
            this.monthLabel = this.ptMonthName(lastIndex);
            this.monthYear = year;
            console.debug(`[Dashboard] Fallback mês escolhido: ${this.monthLabel}/${year} total:`, total);
          } else {
            this.monthLabel = undefined;
            this.monthYear = undefined;
          }
        }
        console.debug('[Dashboard] Produção mês total calculada:', total);
        this.stats.producaoMes = Number(total.toFixed(1));
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dados do dashboard:', this.formatError(err));
        // Não derruba o dashboard: mantém produção como 0 e segue
        this.stats.producaoMes = 0;
        this.loading = false;
      }
    });
  }

  private extractMonthValue(map: Record<string, number>, monthIndex: number): number {
    if (!map) return 0;
    const english = ['JANUARY','FEBRUARY','MARCH','APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER'];
    const portuguese = ['JANEIRO','FEVEREIRO','MARÇO','ABRIL','MAIO','JUNHO','JULHO','AGOSTO','SETEMBRO','OUTUBRO','NOVEMBRO','DEZEMBRO'];
    const pNoAccent = portuguese.map(k => this.removeAccents(k));
    const idx = monthIndex;
    const candidates = [
      english[idx], english[idx].toLowerCase(), this.capitalize(english[idx].toLowerCase()),
      portuguese[idx], portuguese[idx].toLowerCase(), this.capitalize(portuguese[idx].toLowerCase()),
      pNoAccent[idx], pNoAccent[idx].toLowerCase(), this.capitalize(pNoAccent[idx].toLowerCase()),
      String(idx + 1), String(idx + 1).padStart(2, '0')
    ];
    for (const key of candidates) {
      const v = (map as any)[key];
      if (typeof v === 'number') return v;
      if (typeof v === 'string') {
        const n = parseFloat(v.replace(',', '.'));
        if (!Number.isNaN(n)) return n;
      }
    }
    const keys = Object.keys(map || {});
    const engPrefix = english[idx].slice(0,3).toLowerCase();
    const found = keys.find(k => (k || '').toLowerCase().startsWith(engPrefix));
    if (found) {
      const v = (map as any)[found];
      if (typeof v === 'number') return v;
      if (typeof v === 'string') {
        const n = parseFloat(v.replace(',', '.'));
        if (!Number.isNaN(n)) return n;
      }
    }
    return 0;
  }

  private removeAccents(s: string): string {
    return s.normalize('NFD').replace(/\p{Diacritic}/gu, '');
  }

  private capitalize(s: string): string {
    return s ? s.charAt(0).toUpperCase() + s.slice(1) : s;
  }

  private ptMonthName(idx: number): string {
    const months = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
    return months[idx] || '';
  }
}
