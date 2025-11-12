import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { ApiarioService, Apiario } from '../../services/apiario.service';
import { ColmeiaService } from '../../services/colmeia.service';

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

  constructor(
    private authService: AuthService,
    private apiarioService: ApiarioService,
    private colmeiaService: ColmeiaService
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getCurrentUser()?.username || 'Usuário';
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.erro = undefined;
    
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
        
        // Simular dados de produção e alertas
        this.stats.producaoMes = Math.floor(Math.random() * 50) + 20; // kg
        this.stats.alertasPendentes = Math.floor(Math.random() * 5); // alertas
        
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Erro ao carregar dados do dashboard:', error);
        this.erro = 'Falha ao carregar dados. Verifique sua sessão e o servidor.';
        this.loading = false;
      }
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}