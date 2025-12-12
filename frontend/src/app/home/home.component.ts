import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiarioService, Apiario as ApiarioSrv } from '../services/apiario.service';
import { ColmeiaService } from '../services/colmeia.service';
import { ProducaoService } from '../services/producao.service';
import { AuthService } from '../auth/auth.service';

// Definindo interfaces para os tipos
interface Apiario {
  id: number;
  nome: string;
  colmeias: number;
  localizacao?: string;
}

interface Colmeia {
  id: number;
  identificacao: string;
  tipo: string;
  status: string;
}

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
  apiarios: Apiario[] = [];
  colmeias: Colmeia[] = [];
  producaoTotal = 0;
  anoAtual = new Date().getFullYear();
  mesAtual = new Date().getMonth() + 1; // 1-12
  carregando = true;
  autenticado = false;
  mostrarRecursos = false;

  constructor(
    private apiarioService: ApiarioService,
    private colmeiaService: ColmeiaService,
    private producaoService: ProducaoService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.autenticado = this.authService.isAuthenticated();
    this.carregarDados();
  }

  ngAfterViewInit(): void {
    // Faz rolagem suave para o fragmento (ex.: #recursos) mesmo sem anchorScrolling global
    this.route.fragment.subscribe(fragment => {
      if (!fragment) return;
      const el = document.getElementById(fragment);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  }

  carregarDados(): void {
    this.carregando = true;
    if (!this.autenticado) {
      // Sem token: não chama backend para evitar 401 e redirecionamento
      this.apiarios = [];
      this.colmeias = [];
      this.producaoTotal = 0;
      this.carregando = false;
      return;
    }
    const ano = this.anoAtual;
    const mes = this.mesAtual;

    forkJoin({
      apiarios: this.apiarioService.getApiarios().pipe(
        catchError(err => {
          console.warn('[Home] Falha ao carregar apiários:', err);
          return of([] as ApiarioSrv[]);
        })
      ),
      colmeias: this.colmeiaService.getColmeias().pipe(
        catchError(err => {
          console.warn('[Home] Falha ao carregar colmeias:', err);
          return of([] as Colmeia[]);
        })
      ),
      producao: this.producaoService.producaoMensalTotal(ano, mes).pipe(
        catchError(err => {
          console.warn('[Home] Falha ao carregar produção total do mês:', err);
          return of({} as Record<string, number>);
        })
      )
    }).subscribe(({ apiarios, colmeias, producao }) => {
      // Mapear apiários para a interface usada no Home
      this.apiarios = (apiarios || []).map((a: ApiarioSrv) => ({
        id: a.id,
        nome: a.nome,
        colmeias: (a.colmeias?.length ?? a.totalColmeias ?? 0),
        localizacao: a.localizacao
      }));

      // Colmeias para lista recente
      this.colmeias = (colmeias || []).slice(0, 10) as Colmeia[];

      // Produção total do mês selecionado (soma dos valores do objeto retornado)
      const vals = Object.values(producao || {});
      const total = vals.reduce((sum, v: any) => {
        const n = typeof v === 'string' ? parseFloat(v.replace(',', '.')) : v;
        return sum + (Number.isFinite(n) ? n : 0);
      }, 0);
      this.producaoTotal = Number((total || 0).toFixed(1));

      this.carregando = false;
    });
  }

  getActiveColmeiasCount(): number {
    return this.colmeias.filter(c => c.status === 'ATIVA').length;
  }

  // trackBy helpers para *ngFor
  trackByApiario(_index: number, apiario: Apiario | undefined): number | undefined {
    return apiario?.id;
  }

  trackByColmeia(_index: number, colmeia: Colmeia | undefined): number | undefined {
    return colmeia?.id;
  }

  scrollTo(id: string): void {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  toggleRecursos(): void {
    this.mostrarRecursos = !this.mostrarRecursos;
  }
}
