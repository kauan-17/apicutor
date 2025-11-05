import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { ApiarioService } from '../../services/apiario.service';
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
    
    // Carregar dados do dashboard
    this.apiarioService.getApiarios().subscribe({
      next: (apiarios) => {
        this.stats.totalApiarios = apiarios.length;
        
        // Contar colmeias de todos os apiários
        let totalColmeias = 0;
        apiarios.forEach(apiario => {
          totalColmeias += apiario.colmeias?.length || 0;
        });
        this.stats.totalColmeias = totalColmeias;
        
        // Simular dados de produção e alertas
        this.stats.producaoMes = Math.floor(Math.random() * 50) + 20; // kg
        this.stats.alertasPendentes = Math.floor(Math.random() * 5); // alertas
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar dados do dashboard:', error);
        this.loading = false;
      }
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}