import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ApiariosService, Colmeia, Inspecao, ProducaoResumo, Tarefa, Alerta, Clima } from '../../services/apiarios.service';
import { ApiarioService } from '../../services/apiario.service';
import { FuncionarioService } from '../../services/funcionario.service';
import { AuthService } from '../../auth/auth.service';
import { RoleVisibilityDirective } from '../../auth/role-visibility.directive';

@Component({
  selector: 'app-apiarios',
  standalone: true,
  imports: [CommonModule, RouterModule, RoleVisibilityDirective],
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiariosService: ApiariosService,
    private apiarioHttp: ApiarioService,
    private funcionarioService: FuncionarioService,
    public authService: AuthService
  ) {
    this.route.params.subscribe(params => {
      const id = params['id'];
      this.selectedId = id ? +id : undefined;
      if (this.selectedId) {
        this.loadData(this.selectedId);
      }
    });

    // Carregar lista de apiários para seleção
    // Se for funcionário, tenta carregar o apiário associado ao usuário
    if (this.authService.hasRole('ROLE_FUNCIONARIO') || this.authService.hasRole('FUNCIONARIO')) {
      const apiarioIdFromToken = this.authService.getCurrentUser()?.apiarioId;
      if (apiarioIdFromToken) {
        this.selectedId = apiarioIdFromToken;
        this.loadData(apiarioIdFromToken);
        this.apiarioHttp.getApiario(apiarioIdFromToken).subscribe(a => (this.apiariosLista = a ? [a] : []));
      } else {
        // Fallback: tenta obter do backend
        this.funcionarioService.getMe().subscribe({
          next: (me: any) => {
            const apiarioId = me?.apiarioId ?? me?.apiario?.id;
            if (apiarioId) {
              this.selectedId = apiarioId;
              this.loadData(apiarioId);
              this.apiarioHttp.getApiario(apiarioId).subscribe(a => (this.apiariosLista = a ? [a] : []));
            } else {
              this.apiarioHttp.getApiarios().subscribe(lista => (this.apiariosLista = lista));
            }
          },
          error: () => {
            this.apiarioHttp.getApiarios().subscribe(lista => (this.apiariosLista = lista));
          }
        });
      }
    } else {
      this.apiarioHttp.getApiarios().subscribe(lista => (this.apiariosLista = lista));
    }
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
}