import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ApiariosService, Colmeia, Inspecao, ProducaoResumo, Tarefa, Alerta, Clima } from '../../services/apiarios.service';
import { ApiarioService } from '../../services/apiario.service';

@Component({
  selector: 'app-apiarios',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './apiarios.component.html',
  styleUrls: ['./apiarios.component.css']
})
export class ApiariosComponent {
  selectedId?: number;
  apiario?: any;
  apiariosLista: any[] = [];
  colmeias: Colmeia[] = [];
  inspecoes: Inspecao[] = [];
  resumo?: ProducaoResumo;
  tarefas: Tarefa[] = [];
  alertas: Alerta[] = [];
  clima?: Clima;
  feedbackMessage?: string;

  constructor(private route: ActivatedRoute, private router: Router, private apiariosService: ApiariosService, private apiarioHttp: ApiarioService) {
    this.route.params.subscribe(params => {
      const id = params['id'];
      this.selectedId = id ? +id : undefined;
      if (this.selectedId) {
        this.loadData(this.selectedId);
      }
    });

    // Carregar lista de apiários para seleção
    this.apiarioHttp.getApiarios().subscribe(lista => (this.apiariosLista = lista));
  }

  private loadData(apiarioId: number) {
    this.apiarioHttp.getApiario(apiarioId).subscribe(a => (this.apiario = a));
    this.apiariosService.getColmeias(apiarioId).subscribe(c => (this.colmeias = c));
    this.apiariosService.getInspecoes(apiarioId).subscribe(i => (this.inspecoes = i));
    this.apiariosService.getResumoProducao(apiarioId).subscribe(r => (this.resumo = r));
    this.apiariosService.getTarefas(apiarioId).subscribe(t => (this.tarefas = t));
    this.apiariosService.getAlertas(apiarioId).subscribe(al => (this.alertas = al));
    this.apiariosService.getClima(apiarioId).subscribe(cl => (this.clima = cl));
  }

  selecionarApiario(id: number) {
    this.router.navigate(['/apiarios', id]);
  }

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
}