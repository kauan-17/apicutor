import { Component, ElementRef, ViewChild } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiariosService, Inspecao, ProducaoResumo, Alerta, Clima } from '../../services/apiarios.service';
import { ApiarioService, Tarefa } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';
import { FuncionarioService } from '../../services/funcionario.service';
import { AuthService } from '../../auth/auth.service';
import { RoleVisibilityDirective } from '../../auth/role-visibility.directive';
import * as L from 'leaflet';
import { ProducaoService } from '../../services/producao.service';

@Component({
  selector: 'app-apiarios',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, RoleVisibilityDirective],
  templateUrl: './apiarios.component.html',
  styleUrls: ['./apiarios.component.css']
})
export class ApiariosComponent {
  selectedId?: number;
  apiario?: any;
  apiariosLista: any[] = [];
  buscaTermo = '';
  carregandoLista = true;
  erroLista?: string;
  colmeias: any[] = [];
  inspecoes: Inspecao[] = [];
  resumo?: ProducaoResumo;
  tarefas: Tarefa[] = [];
  alertas: Alerta[] = [];
  clima?: Clima;
  feedbackMessage?: string;
  carregandoDetalhe = false;

  leafReady = false;
  leafletMap?: L.Map;
  leafletMarker?: L.Marker;
  @ViewChild('leafletMap') leafletMapDiv?: ElementRef<HTMLDivElement>;
  mapCenter: { lat: number; lng: number } = { lat: -15.78, lng: -47.93 }; // default Brasilia
  mapZoom = 13;

  novaTarefaTitulo = '';
  novaTarefaPrazo?: string;
  novaTarefaStatus: 'Pendente' | 'Em andamento' | 'Concluída' = 'Pendente';

  get selectedApiarioName(): string | undefined {
    const id = this.selectedId;
    if (!id) return undefined;
    const a = this.apiariosLista?.find(x => x?.id === id);
    return a?.nome;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiariosService: ApiariosService,
    private apiarioHttp: ApiarioService,
    private colmeiaHttp: ColmeiaService,
    private funcionarioService: FuncionarioService,
    public authService: AuthService,
    private producaoService: ProducaoService
  ) {

    this.route.params.subscribe(params => {
      const id = params['id'];
      this.selectedId = id ? +id : undefined;
      if (this.selectedId) {
        this.loadData(this.selectedId);
      }
    });

    // Carregar lista de apiários para seleção (sem auto seleção para funcionários)
    // Backend já filtra os apiários visíveis conforme a permissão
    this.carregandoLista = true;
    this.apiarioHttp.getApiarios().pipe(
      catchError(err => {
        console.warn('[Apiários] Falha ao carregar lista:', err);
        this.erroLista = 'Falha ao carregar apiários. Tente novamente mais tarde.';
        return of([]);
      })
    ).subscribe(lista => {
      this.apiariosLista = lista ?? [];
      this.carregandoLista = false;
    });
  }

  ngAfterViewInit(): void {
    this.route.fragment.subscribe(fragment => {
      if (!fragment) return;
      const el = document.getElementById(fragment);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  }

  private loadData(apiarioId: number) {
    this.carregandoDetalhe = true;
    const ano = new Date().getFullYear();
    const mes = new Date().getMonth() + 1;
    forkJoin({
      apiario: this.apiarioHttp.getApiario(apiarioId).pipe(catchError(err => {
        console.warn('[Apiários] Falha ao carregar apiário:', err);
        return of(null);
      })),
      colmeias: this.colmeiaHttp.getColmeiasByApiario(apiarioId).pipe(catchError(err => {
        console.warn('[Apiários] Falha ao carregar colmeias:', err);
        return of([]);
      })),
      inspecoes: this.apiariosService.getInspecoes(apiarioId).pipe(catchError(() => of([]))),
      prodAno: this.producaoService.producaoMensal(apiarioId, ano).pipe(catchError(() => of({} as Record<string, number>))),
      prodMes: this.producaoService.producaoMensal(apiarioId, ano, mes).pipe(catchError(() => of({} as Record<string, number>))),
      tarefas: this.apiarioHttp.getTarefas(apiarioId).pipe(catchError(() => of([]))),
      alertas: this.apiariosService.getAlertas(apiarioId).pipe(catchError(() => of([]))),
      clima: this.apiariosService.getClima(apiarioId).pipe(catchError(() => of(undefined)))
    }).subscribe(({ apiario, colmeias, inspecoes, prodAno, prodMes, tarefas, alertas, clima }) => {
      this.apiario = apiario || undefined;
      this.colmeias = colmeias || [];
      this.inspecoes = inspecoes || [];
      const sumValues = (map: Record<string, number>) => Object.values(map || {}).reduce((s, v) => s + (typeof v === 'number' ? v : 0), 0);
      const anoTotal = Number(sumValues(prodAno).toFixed(1));
      const mesTotal = Number(sumValues(prodMes).toFixed(1));
      const media = this.colmeias.length ? Number((mesTotal / this.colmeias.length).toFixed(2)) : 0;
      let melhorMesLabel: string | undefined = undefined;
      const months = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
      let maxVal = -1; let maxIdx = -1;
      for (let i = 0; i < 12; i++) {
        const v = (prodAno as any)[months[i]] ?? (prodAno as any)[months[i].toUpperCase()] ?? (prodAno as any)[String(i + 1)] ?? 0;
        const n = typeof v === 'number' ? v : 0;
        if (n > maxVal) { maxVal = n; maxIdx = i; }
      }
      if (maxIdx >= 0) melhorMesLabel = months[maxIdx];
      this.resumo = {
        apiarioId,
        anoAtualKg: anoTotal,
        mesAtualKg: mesTotal,
        mediaKgPorColmeia: media,
        melhorMes: melhorMesLabel
      };
      this.tarefas = tarefas || [];
      this.alertas = alertas || [];
      this.clima = clima;

      const lat = Number(apiario?.latitude);
      const lng = Number(apiario?.longitude);
      if (Number.isFinite(lat) && Number.isFinite(lng)) {
        this.mapCenter = { lat, lng };
        setTimeout(() => this.initLeafletMap(), 0);
      }

      this.carregandoDetalhe = false;
    });
  }

  private initLeafletMap(): void {
    const container = this.leafletMapDiv?.nativeElement || document.getElementById('leafletMap');
    if (!container) return;
    // Destroy previous
    if (this.leafletMap) {
      this.leafletMap.remove();
      this.leafletMap = undefined;
    }
    const { lat, lng } = this.mapCenter;
    this.leafletMap = L.map(container).setView([lat, lng], this.mapZoom);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.leafletMap);
    const icon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });
    this.leafletMarker = L.marker([lat, lng], { icon, title: this.apiario?.nome || 'Apiário' }).addTo(this.leafletMap);
    this.leafReady = true;
  }

  adicionarTarefa(): void {
    const id = this.selectedId;
    const titulo = (this.novaTarefaTitulo || '').trim();
    if (!id || !titulo) return;
    this.apiarioHttp.createTarefa({ apiarioId: id, titulo, prazo: this.novaTarefaPrazo, status: this.novaTarefaStatus }).subscribe({
      next: (t) => {
        this.tarefas = [t, ...(this.tarefas || [])];
        this.novaTarefaTitulo = '';
        this.novaTarefaPrazo = undefined;
        this.novaTarefaStatus = 'Pendente';
        this.feedbackMessage = 'Tarefa adicionada.';
        setTimeout(() => (this.feedbackMessage = undefined), 2000);
        // Notifica dashboard para recarregar próxima tarefa
        try { this.apiariosService.tarefasChanged$.next(); } catch {}
      }
    });
  }

  selecionarApiario(id: number) {
    this.router.navigate(['/apiarios', id]);
  }

  get apiariosFiltrados(): any[] {
    const termo = (this.buscaTermo || '').toLowerCase().trim();
    if (!termo) return this.apiariosLista;
    return this.apiariosLista.filter(a => {
      const nome = (a?.nome || '').toLowerCase();
      const loc = (a?.localizacao || '').toLowerCase();
      return nome.includes(termo) || loc.includes(termo);
    });
  }

  trackByApiario(_index: number, a: any): number | undefined { return a?.id; }

  novaInspecao() {
    const nova: Inspecao = {
      id: Math.floor(Math.random() * 1000000),
      data: new Date().toISOString(),
      responsavel: 'Usuário',
      observacoes: 'Inspeção rápida criada pela interface.',
      colmeiasVerificadas: this.colmeias.length || (this.apiario?.totalColmeias ?? 0)
    };
    this.inspecoes = [nova, ...this.inspecoes];
    this.feedbackMessage = 'Inspeção criada com sucesso.';
    setTimeout(() => (this.feedbackMessage = undefined), 3000);
  }

  excluirApiario() {
    if (!this.selectedId) return;
    const confirma = window.confirm('Tem certeza que deseja excluir este apiário? Esta ação não pode ser desfeita.');
    if (!confirma) return;
    this.apiarioHttp.deleteApiario(this.selectedId).subscribe({
      next: () => {
        this.feedbackMessage = 'Apiário excluído com sucesso.';
        // Recarrega lista e volta para visão de seleção
        this.apiarioHttp.getApiarios().subscribe(lista => (this.apiariosLista = lista));
        this.router.navigate(['/apiarios']);
        setTimeout(() => (this.feedbackMessage = undefined), 3000);
      },
      error: () => {
        this.feedbackMessage = 'Falha ao excluir apiário. Verifique permissões.';
        setTimeout(() => (this.feedbackMessage = undefined), 3000);
      }
    });
  }

  getActiveCount(): number {
    if (!this.colmeias?.length) return 0;
    return this.colmeias.filter(c => {
      const s = (c?.status || '').toString().toUpperCase();
      return s === 'ATIVA';
    }).length;
  }

  getInactiveCount(): number {
    if (!this.colmeias?.length) return 0;
    return this.colmeias.filter(c => {
      const s = (c?.status || '').toString().toUpperCase();
      return s === 'INATIVA';
    }).length;
  }

  hasAnyData(): boolean {
    const temColmeias = !!(this.colmeias && this.colmeias.length);
    const temInspecoes = !!(this.inspecoes && this.inspecoes.length);
    const temTarefas = !!(this.tarefas && this.tarefas.length);
    const temAlertas = !!(this.alertas && this.alertas.length);
    const prodAno = Number(this.resumo?.anoAtualKg || 0);
    const prodMes = Number(this.resumo?.mesAtualKg || 0);
    const temProducao = prodAno > 0 || prodMes > 0;
    return temColmeias || temInspecoes || temTarefas || temAlertas || temProducao;
  }
}
